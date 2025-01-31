# Signed Paintings

Bring your own custom images into the game as vanilla-like paintings using signs - anyone else with the mod will see them too! Just paste in an image URL into an empty sign!

Tweak the size and positioning of the painting within the sign's UI and see the changes live.

The mod can automatically scale the images to make them pixel consistent with Minecraft so they fit in - even for higher resolution texture packs

Avoid having to make map art!

![Gif showing the positioning and sizing of an image](images/gif.gif)

## Features

- Render web images on signs, hanging signs - just copy in the link!
- Rename banners and shields to display web images on them
- Optional automatic pixelization to keep consistency with Minecraft's pixel size
- Frame options using any block texture
- In-game Imgur upload if the link is too long
- Tweak sizing, alignment and offset

![Showcase showing the main features of the mod](images/combined.png)

## Commands:

`/paintings:toggle all|banners|shields|signs` - toggles rendering for images

`/paintings:toggle culling` - makes signs visible from further away, at the cost of worse fps - use [this mod](https://modrinth.com/mod/beer) instead if you want a bit more control over the distance

`/paintings:reload all|<url>` - redownload all or a specific image

`/paintings:status all|<url>` - gets info about what images are currently loaded

`/paintings:upload <url>` - upload an image from a url to imgur

if someone is being annoying, you can stop specific links from rendering:

`/paintings:block add <url>` - blocks a specific image, stopping it rendering

`/paintings:block remove all|<url>` - unblocks a specific image or all blocked images

`/paintings:block list` - lists all blocked links

`/paintings:block auto` - automatically blocks newly loading links

## FAQ

I use a high-re texture pack, does the pixel filter support this?

> Yes! The pixel filter supports any resolution! Just move the slider or change the number.

Do I need to be connected to the internet to use the mod?

> Yes, the mod downloads images from the Internet so a connection is a must! (loaded images will be cached for your current session, instead of redownloading for every individual sign)

Will my friends see the custom painting?

> As long as your friends have the mod they will see exactly the same thing you do!

What if my friends don't have the mod?

> They will see a sign with a URL and some text for the settings. However, you can hide the sign underground then shift the painting up using the offset slider (the text may also be compressed, in which case it would look like a bunch of thin lines)

Fabric? Forge? Quilt? Backports?

> The mod is developed for the latest versions Fabric, and while untested for Quilt it should work - Forge is currently unsupported, [Sinytra Connector](https://modrinth.com/mod/connector) may allow it to work on forge, but dont be suprised if you encounter bugs!