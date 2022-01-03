# Launchpad util
If you have launchpad, but it's just a midi device. So what if you can use them as macro? 
of course launchpad mini can be 80 key macro (for now)

## WIP
This project is still work in progress (on free time) any suggestion please open issue
- [x] can be macro
  - [x] via hardcoded
  - [x] via config file (unstable and no docs)
- [x] draw gif
- [x] draw string (only ascii other language is very hard to read)
- [ ] multiple instance support
- [ ] another launchpad support (currently made for launchpad mini)
- [ ] docs / wiki
- [ ] scripting support
  - [ ] js
  - [ ] kts

## Internal
+ Use kotlin coroutines to fire event to make it cancellable
+ Use yml in config file