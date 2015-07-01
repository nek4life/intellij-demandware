# Intellij Demandware Plugin

## Features
 * Project wizard to setup project or new module with ease.
 * Sandbox File Uploading. Module Files are synced when they are modified and saved.
 * File upload console logging to verify file was modified or created successfully.
 * Auto completion for ISML tags, attributes, and known valid attribute values.
 * Live template shortcuts for ISML tags.

## Requirements
This plugin requires Java JDK8 to work correctly. This means that you will need to download
a build of Intellij that has the JDK8 bundled on Mac or have JDK8 installed on your machine
if you run Windows.

## Build
To run and debug this plugin you will need to follow the instructions provided in the Intellij plugin
wiki http://www.jetbrains.org/intellij/sdk/docs/basics/getting_started/setting_up_environment.html.
You will also need to mark the resources folder as resources to include them in the plugin build.

To create the plugin jar select "Prepare Plugin Module 'intellij-demandware' For Deployment from the
build menu. This will create the intellij-demandware.jar in your project folder.

## Installation
Now you can install the intellij-demandware.jar through the plugin preferences by using the
'Install plugin from disk' option.

In the future this plugin may be added to the plugin repo
for installation through the plugin manager using the 'Browse repositories...' option.
