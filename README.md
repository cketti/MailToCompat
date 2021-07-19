# MailToCompat

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/de.cketti.mailto/mailto-compat/badge.svg)](https://maven-badges.herokuapp.com/maven-central/de.cketti.mailto/mailto-compat)

A drop-in replacement for Android's buggy [`MailTo`](https://developer.android.com/reference/android/net/MailTo) class to parse `mailto:` URIs.

**DEPRECATED**: Use [androidx.core.net.MailTo](https://developer.android.com/reference/kotlin/androidx/core/net/MailTo) instead. Read [android.net.MailTo is broken](https://cketti.de/2020/06/22/android-net-mailto-is-broken/) for more details.

## Include the library

Add this to your `dependencies` block in `build.gradle`:

```groovy
implementation 'de.cketti.mailto:mailto-compat:1.0.0'
```


## Usage

Replace `import android.net.MailTo` with `import de.cketti.mailto.MailTo` throughout your project and you should be good to go.

In addition to the methods implemented by [`android.net.MailTo`](https://developer.android.com/reference/android/net/MailTo) this library supports
the `getBcc()` method to retrieve BCC recipients when specified in a mailto URI.

## License

    Copyright (C) 2020 cketti
    Copyright (C) 2008 The Android Open Source Project

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
