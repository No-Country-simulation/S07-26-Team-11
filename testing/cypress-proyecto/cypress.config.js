const { defineConfig } = require("cypress");

module.exports = defineConfig({

  allowCypressEnv: false,

  e2e: {

    baseUrl: 'https://capacia.vercel.app',

    defaultCommandTimeout: 15000,

    setupNodeEvents(on, config) {

      // implement node event listeners here

    },

  },

});

