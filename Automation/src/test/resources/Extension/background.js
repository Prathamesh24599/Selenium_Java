chrome.webRequest.onAuthRequired.addListener(
  function (details, callbackFn) {
    return {
      authCredentials: {
        username: "lgzlswko",
        password: "m9s7add53nqz"
      }
    };
  },
  { urls: ["<all_urls>"] }, // Changed to apply to all URLs
  ["blocking"]
);

chrome.proxy.settings.set({
  value: {
    mode: "fixed_servers",
    rules: {
      singleProxy: {
        scheme: "http",
        host: "38.154.227.167",
        port: parseInt("5868")
      }
    }
  },
  scope: "regular"
});