#!/bin/bash

echo "🔐 Fetching secrets from GitHub..."


MONGODB_URI=$(gh secret view MONGODB_URI --repo Seoul-Squad/PlanMate --json value -q .value)

if [ -z "$MONGODB_URI" ]; then
  echo "❌ MONGODB_URI not found or couldn't be fetched. Make sure you're authenticated with 'gh auth login'."
  exit 1
fi

echo "MONGODB_URI=${MONGODB_URI}" > .env
echo "✅ .env file created successfully!"
