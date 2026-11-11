# Poplar Log Display Minecraft Plugin

Poplar Log Display is a Minecraft plugin for Paper that adds a placeable log display block using a marked birch log item and a BlockDisplay entity. It provides a command to get the item, lets players place it in an adjacent air block, and lets them pick it up by interacting with the display.

## Install

1. Build the project with Java 21 and Maven using `mvn package`.
2. Copy `target/plugin.jar` into your Paper server's `plugins` directory.
3. Start or restart the server.

## Use

1. Run `/poplarlog` to receive a Poplar Log Display item. The `poplardisplay.give` permission is granted by default.
2. Hold the item and right-click a block to place the display in the air block beside it.
3. Right-click the display entity to remove it and receive the item back.

The display currently uses birch log block data as its appearance; it does not add a native poplar block. Damaging a display removes it without returning an item. Displays are also removed when the plugin disables, including during server shutdown or reload.

---

More Minecraft plugin projects by EnderDevelopment: https://github.com/EnderDevelopment

Project page: https://dash.enderdevelopment.com/community/6d28e4fd-3314-4085-be68-33630afcdd1a?utm_source=github&utm_medium=readme&utm_campaign=auto_publish&utm_content=6d28e4fd-3314-4085-be68-33630afcdd1a
