const PubNub = require("pubnub");
const initializationData = require("./initialization-data");
const { SingleBar, Presets } = require("cli-progress");
const prompts = require("prompts");
const fs = require("file-system");
const dotenv = require("dotenv");
const expand = require("dotenv-expand");
const mime = require('mime-types');

const keyPrompt = `
*** A PubNub account is required. ***
Visit the PubNub dashboard to create an account or login.
     https://dashboard.pubnub.com/
Create a new chat app or locate your chat app in the dashboard.
Copy and paste your publish key and then your subscribe key below.
`;

const PROPERTIES = "../gradle.properties";
const PUBLISH_KEY = 'com.pubnub.publishKey'
const SUBSCRIBE_KEY = 'com.pubnub.subscribeKey'
const CIPHER_KEY = 'com.pubnub.cipherKey'

let errorCount = 0;

const hasValidUrlProtocol = (url) =>
    Boolean(['http://', 'https://', 'ftp://'].some(protocol => url.startsWith(protocol)))

// group into batches of size
const batch = (list, size) => {
  // split into batches
  return list.reduce(
    (batched, item, index, items) => {
      batched.current.push(item);
      // move complete batches out
      if ((index > 0 && index % size === 0) || index === items.length - 1) {
        batched.complete.push(batched.current);
        batched.current = [];
      }
      return batched;
    },
    { complete: [], current: [] }
  ).complete;
};

//  invoke f on all members of the each batch sequentially
const doBatches = async (batches, f) => {
  for (const batch of batches) {
    await Promise.all(batch.map(f));
  }
};

const getKeys = async () => {
  // check current environment variables in gradle.properties
  const config = dotenv.parse(fs.readFileSync(PROPERTIES))
  const env = expand(config);
  for (const k in config) {
    process.env[k] = config[k]
  }

  if (
    env[PUBLISH_KEY] &&
    env[SUBSCRIBE_KEY]
  ) {
    if (process.argv[2] === "--quick-test") {
      console.log("Keys detected in "+PROPERTIES);
      process.exit(0);
    }
    return {
      publishKey: env[PUBLISH_KEY],
      subscribeKey: env[SUBSCRIBE_KEY],
      cipherKey: env[CIPHER_KEY]
    };
  }
  // prompt
  console.log(keyPrompt);
  const result = await prompts([
    {
      type: "text",
      name: "publishKey",
      message: "Enter your publish key",
      validate: key => (key.startsWith("pub-") ? true : "Invalid publish key")
    },
    {
      type: "text",
      name: "subscribeKey",
      message: "Enter your subscribe key",
      validate: key => (key.startsWith("sub-") ? true : "Invalid subscribe key")
    },
    {
      type: "text",
      name: "cipherKey",
      message: "Enter your cipher key or skip"
    }
  ]);
  // append to gradle.properties
  fs.writeFileSync(
    PROPERTIES,
    `\n# PubNub Keys\n${PUBLISH_KEY}=${result.publishKey}\n${SUBSCRIBE_KEY}=${result.subscribeKey}\n${CIPHER_KEY}=${result.cipherKey}\n`,
    { flag: "a" }
  );
  console.log("\n Your keys have been saved to "+PROPERTIES);
  return {
    publishKey: result.publishKey,
    subscribeKey: result.subscribeKey,
    cipherKey: result.cipherKey
  };
};

const formatError = e =>
  `${e.name}(${e.status.operation}): ${e.status.category}.${e.status.errorData.code}`;

const initializeMessages = (pubnub, status) => async ({
  ...message
}) => {
  try {
    // upload file if image path is set to local
    if(message.custom.image && !hasValidUrlProtocol(message.custom.image)){
        const file = fs.createReadStream(message.custom.image);
        const fileName = file.path.replace(/^.*[\\\/]/, "");
        const mimeType = mime.lookup(message.custom.image);
        const channel = message.channelId;

          const uploadResult = await pubnub.sendFile({
            channel: channel,
            file: { stream: file, name: fileName, mimeType: mimeType }
          });

        const fileUrl = pubnub.getFileUrl({ channel: channel, id: uploadResult.id, name: uploadResult.name });
        // override image path with uploaded one
        message.custom.image = fileUrl;
    }

    const response = await pubnub.publish({
      channel: message.channelId,
      message: message
    });
    if (response.error) {
      errorCount++;
      console.error(`Unknown error initializing data for ${response}.`);
    } else {
      status.increment();
    }
  } catch (e) {
    errorCount++;
    console.error(e);
    console.error(formatError(e));
  }
};

const initializeChannelGroup = (pubnub, status) => async ({
  group: channelGroup,
  channels
}) => {
if(channels.length == 0){
        status.increment();
        } else {
  try {
    await pubnub.channelGroups.addChannels({
      channelGroup,
      channels
    });
    status.increment();
  } catch (e) {
    errorCount++;
    console.error(formatError(e));
  }
  }
};

const initializeMembership = (pubnub, status) => async ({
  space: channel,
  members: uuids
}) => {
  try {
    const response = await pubnub.objects.setChannelMembers({
      channel,
      uuids
    });

    if (response.status === 200) {
      status.increment(uuids.length);
    } else {
      errorCount++;
      console.error(`Unknown error initializing members for ${channel}.`);
    }
  } catch (e) {
    errorCount++;
    console.error(formatError(e));
  }
};

// the bars need a second to update
const sleep = async ms => {
  return new Promise(resolve => {
    setTimeout(resolve, ms);
  });
};

const main = async () => {
  // get pubsub keys
  const keys = await getKeys();
  const pubnub = new PubNub({
    ...keys
//    logVerbosity: true
  });

  console.log("\nInitializing");
  // setup progress bars
  const totalChannelGroups = initializationData.channelGroups.length;
  const totalMessages = initializationData.messages.length;

  const channelGroupCreationStatus = new SingleBar({}, Presets.shades_classic);
  const messageCreationStatus = new SingleBar({}, Presets.shades_classic);

  // initialize data
  console.log("\nInitializing Channel Groups:");
  channelGroupCreationStatus.start(totalChannelGroups, 0);
  await doBatches(
    batch(initializationData.channelGroups, 1),
    initializeChannelGroup(pubnub, channelGroupCreationStatus)
  );
  await sleep(100);
  console.log("\n");


  console.log("\nInitializing Messages:");
  messageCreationStatus.start(totalMessages, 0);
  await doBatches(
    batch(initializationData.messages, 1),
    initializeMessages(pubnub, messageCreationStatus)
  );
  await sleep(100);
  console.log("\n");

  if (errorCount === 0) {
    process.exit(0);
  } else {
    console.warn(
      `${errorCount} error${
        errorCount === 1 ? "" : "s"
      } initializing data. \n Please "npm run setup" again.`
    );
    process.exit(1);
  }
};

main();