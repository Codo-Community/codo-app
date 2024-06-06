/** @type import('hardhat/config').HardhatUserConfig */
// hardhat.config.js
require('@nomicfoundation/hardhat-viem');

module.exports = {
  networks: {
    hardhat: {
      chainId: 31337
    }
  }
};


