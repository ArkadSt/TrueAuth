# TrueAuth
TrueAuth is an authentication plugin for Spigot and Paper that I have been developing for my offline-mode Minecraft Server<br/>
This plugin is a bit different from those authentication plugins that I know.

## How it works
1. When player joins the server, his playerdata is being saved and copied to plugin's folder.
2. Player appears at spawn or at specified location by default and there is no option for player to appear at his last location, unless his session is active.
3. Player appears with empty inventory, no anvancements, no unlocked recipes, no stats and with zero experience points.
4. A special mode is set for unauthorized player in which it is impossible to die or interact with the world in any way.
5. As soon as you ender your password, you are being kicked from the server, your original playerdata is being restored and your session becomes active.
6. Then you can rejoin and play.

By restoring original playerdata, instead of just teleporting, it is possible to preserve all player states, such as being in a boat, riding a horse, or flying with elytra.

## Session
Session is a period of time when you can join the server without having to authorize. Session time can be configured by editing config.yml

Session becomes active when you:
1. Are being kicked after successful authentication.<br/>
2. Leave the server<br/>

## Commands
### General commands:
`/register password password`<br/>
`/login password`<br/>
`/changepassword new_password new_password`

You can also use `/reg`, `/l` and `/changepass`.

### Admin commands:
`/trueauth reload` - reloads config<br/>
