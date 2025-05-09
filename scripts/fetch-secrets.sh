#!/bin/bash

echo "🔐 Fetching secrets from GitHub..."

# Fetch secret from GitHub
MONGODB_URI=$(gh secret view MONGODB_URI --repo Seoul-Squad/PlanMate --json value -q .value)

# Validate
if [ -z "$MONGODB_URI" ]; then
  echo "❌ MONGODB_URI not found. Run 'gh auth login' to authenticate GitHub CLI."
  exit 1
fi

# Save to .env
echo "MONGODB_URI=${MONGODB_URI}" > .env
echo "✅ .env file created successfully!"
