# Namer Scoreboards

Based on [Chyzman's Namer mod](https://github.com/chyzman/namer), Namer Scoreboards adds the functionality I needed for my community scoreboard, for nicknames to be displayed on scoreboards, nametags and in chat.

It also allows for on-the-fly config changes.

### Commands:

- `/namerscoreboards set` and `/namerscoreboards clear` to set and clear your own nickname
- `/namerscoreboards setPlayerNick` and `/namerscoreboards clearPlayerNick` to set and clear another player's nickname
- `/namerscoreboards allowSettingOwnNicknames <enabled|disabled>` to enable/disable players setting their own nicknames (handy if your players are pests like mine)
- `/namerscoreboards maxNickLength <0-255>` to set the maximum nickname length (0 = no limit). Note: this, annoyingly, includes formatting tags. I'll work on that for an update.
- `/namerscoreboards allowNickFormatting <enabled|disabled>` to enable/disable formatting in nicknames (SFT/MiniMessage formatting seem to work)

Players' nicknames will appear on scoreboards, their nametags, in chat and on the player list, and can be used to reference them in commands.

Namer Scoreboards is licenced under the GNU General Public License, as per the original Namer mod.
