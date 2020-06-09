#!/bin/bash
set -e

# Generate API docs
./gradlew dokka

# Copy *.md files into docs directory
cp README.md docs/index.md
cp CHANGELOG.md docs/changelog.md
