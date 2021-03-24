# [buildAPKs.apps](https://github.com/BuildAPKs/buildAPKs.apps)
Android APK app sources that build in [Termux](https://github.com/termux) in Amazon, Android and Chrome. 

[This repository](https://github.com/BuildAPKs/buildAPKs.apps) is a submodule for [buildAPKs](https://github.com/BuildAPKs/buildAPKs).  In order to install it in its' proper place to attempt to make these applications on smartphone, smartTV, tablet and wearable, copy and paste the following into [Termux](https://github.com/termux) on Android:

```

   apt install curl 

   curl -O https://raw.githubusercontent.com/BuildAPKs/buildAPKs/master/setup.buildAPKs.bash

   bash setup.buildAPKs.bash

   ~/buildAPKs/build.apps.bash

```
To see all the possible APK projects in this git repository, run [` cat ma.bash `](https://raw.githubusercontent.com/BuildAPKs/buildAPKs.apps/master/ma.bash) in the root directory of this repository after cloning.   Enjoy!
<!--README.md EOF-->
