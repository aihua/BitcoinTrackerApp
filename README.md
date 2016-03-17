# BitcoinTracker Android app
Demo app for live tracking bitcoin / USD market price with "go to past" function

<img src="https://github.com/ramden/BitcoinTrackerApp/raw/master/images/bitcointracker.png" alt="alt text" height="500">

## Libraries and tools included

- Support libraries
- [RxJava](https://github.com/ReactiveX/RxJava) and [RxAndroid](https://github.com/ReactiveX/RxAndroid) 
- [Retrofit 2](http://square.github.io/retrofit/)
- [Dagger 2](http://google.github.io/dagger/)
- [Realm](https://github.com/realm/realm-java)
- [Butterknife](https://github.com/JakeWharton/butterknife)
- Functional tests with [Espresso](https://code.google.com/p/android-test-kit/wiki/Espresso)
- [Robolectric](http://robolectric.org/)
- [Mockito](http://mockito.org/)

## Notes

- UI testing missing
- Instrumentation testing in progress
- Write more unit tests
- Find a better api that supports getting incremental data (currently always fetching all data)
- UI -> Improve chart to have date columns (better UX)

## License

```
    Copyright 2015 Denis Ramic

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
```
