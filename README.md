# softeng306-p1g8
Group 8 - Team Name is Trivial and Left as an Exercise for the Reader

This project is our implementation of the task scheduling system described by the SOFTENG 306 notes. Our implementation is a multi-algorithm approach; when visualization is requested A* is used, branch and bound is used as a fall-back if the system runs out of memory. When visualisation is not needed or multiple threads are requested branch and bound is used, as on most graphs it is faster than our A* implementation.

The system is made up of multiple modules that can fit together to form the used algorithm. There are three heuristics, three child schedule generators, three storage systems and two algorithm implementations. This leads to a wide variety of customization for the specific environment that the system is run in.

The current executable Jar runs using the combination of these modules that leads to the best combination of speed and memory usage.

[Wiki / Documentation](https://github.com/liamtbrand/softeng306-p1g8/wiki)

Minutes, documentation and plans can be found on the wiki, task assignement is found in the issue tracker.

# Team
| GitHub Username | Full Name | UPI |
| --------------- | --------- | --- |
| liamtbrand | Liam Brand | LBRA112 |
| Brownshome | James Brown | JBRO801 |
| mollyfarrant | Molly Farrant | MFAR672 |
| robbierew | Robert Rewcastle | RREW898 |
| cyrus-raitava | Cyrus Raitava-Kumar | CRAI897 |

# Building and Running
The project is built using gradle.

| Action | Command | Output |
| ------ | ------- | ------ |
| Build | `gradlew shadowJar` | `build/libs/` |
| Test | `gradlew test` | Command Line |
| Run | `gradlew run --args 'ARGUMENTS TO PASS'` | Command Line |
