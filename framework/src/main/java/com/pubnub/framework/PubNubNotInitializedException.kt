package com.pubnub.framework

class PubNubNotInitializedException :
    Exception("PubNub instance not initialized. Did you forget to call `PubNubFramework.initialize`?")