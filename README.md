
# Nicknames Everywhere!

Simple, powerful <font color="#FFFF00">f</font><font color="#CCFF33">o</font><font color="#99FF66">r</font><font color="#66FF99">m</font><font color="#33FFCC">a</font><font color="#00FFFF">t</font><font color="#33CCFF">t</font><font color="#6699FF">a</font><font color="#9966FF">b</font><font color="#CC33FF">l</font><font color="#FF00FF">e</font> nicknames and pronouns that display everywhere I could think of: nametags, chat, scoreboards, in commands, on the player list etc. Even comes with a FancyMenu placeholder!

## Features

- Set separate nicknames and pronouns
- Supports colourful formatting (I recommend https://www.birdflop.com/resources/rgb/ - use Colour Format -> MiniMessage)
- Enable/disable players being able to set their own nicknames and pronouns
- Set other players' nicknames and pronouns (requires permission level 2)
- Enable/disable nickname and pronoun formatting
- Set maximum lengths for nicknames and pronouns

### FancyMenu integration

NicknamesEverywhere includes native placeholder support for the **FancyMenu** mod, allowing you to dynamically display player nicknames and pronouns directly inside custom menus, layouts, and text elements:

> <font color="#7FEBE6">{</font><font color="#EB7F7F">"placeholder"</font><font color="#7F9EEB">:</font><font color="#E7C67D">"nicknameseverywhere_nickname"</font><font color="#7F9EEB">,</font><font color="#EB7F7F">"values"</font><font color="#7F9EEB">:</font><font color="#7FEBE6">{</font><font color="#EB7F7F">"username"</font><font color="#7F9EEB">:</font><font color="#E7C67D">"Joel4848"</font><font color="#7F9EEB">,</font><font color="#EB7F7F">"include_pronouns"</font><font color="#7F9EEB">:</font><font color="#E7C67D">"true"</font><font color="#7FEBE6">}}</font>
- Returns a player's formatted nickname (and optionally their pronouns)

## Commands

(All commands can use the alias `/nne`)

### Client

- `/nicknames setNick` and `/nicknames clearNick` to set and clear your nickname
- `/nicknames setPronouns` and `/nicknames clearPronouns` to set and clear your pronouns

### Admin

- `/nicknames admin setPlayerNick` and `/nicknames admin clearPlayerNick` to set and clear another player's nickname
- `/nicknames admin setPlayerPronouns` and `/nicknames admin clearPlayerPronouns` to set and clear another player's pronouns

### Config

- `/nicknames config allowNickFormatting <enabled|disabled>` to enable/disable formatting in nicknames/pronouns
- `/nicknames config allowSettingOwnNicknames <enabled|disabled>` to enable/disable players setting their own nicknames/pronouns
- `/nicknames config maxNickLength <0-255>` to set the maximum nickname length (0 = no limit)
- `/nicknames config maxPronounsLength <0-255>` to set the maximum pronouns length (0 = no limit)
- `/nicknames config usePronounsEverywhere <enabled|disabled>`  if disaabled, pronouns won't show in things like `/tellraw` or `/title` commands (I needed this for my server)
## License

[CC BY-NC-SA 4.0](https://creativecommons.org/licenses/by-nc-sa/4.0/)
