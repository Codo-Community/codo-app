#!/usr/bin/env sh

# Base64 encoded seed input
SEED_BASE64="$1"

# Check if seed is provided
if [ -z "$SEED_BASE64" ]; then
  echo "Usage: ./generate-did.sh <base64_seed>"
  exit 1
fi

# Create an ES module script to generate the DID
cat << 'EOF' > generate-did.mjs
import { Ed25519Provider } from 'key-did-provider-ed25519';
import { DID } from 'dids';
import { fromString } from 'uint8arrays';

(async () => {
  try {
    const seedBase64 = process.argv[2];
    const seed = fromString(seedBase64, 'base64');
    const provider = new Ed25519Provider(seed);
    const did = new DID({ provider });
    await did.authenticate();
    console.log("Generated DID:", did.id);
  } catch (error) {
    console.error("Error generating DID:", error.message);
  }
})();
EOF

# Run the Node.js script with the provided seed
node generate-did.mjs "$SEED_BASE64"

# Cleanup
rm generate-did.mjs
