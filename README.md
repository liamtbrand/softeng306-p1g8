# softeng306-p1g8
#### Winners of the Visualisation Contest, and Second Place in the Best Average Runtime, out of ~15/16 teams of 5, within the 2018 University of Auckland Software Engineering Part III cohort.
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

# Visualisation

![alt text](https://github.com/liamtbrand/se306p1g8/wiki/VISUALISATION.png "The main visualisation, of the Algorithm's Search Space.")
_A visualisation of the search space of the algorithm, where the horizontal deviations refer to decisions made as to which tasks are processed when._

***

![alt text](https://github.com/liamtbrand/se306p1g8/wiki/SCHEDULE.png "Another visualisation tab, where the schedule currently being analysed is shown, alongside statistics on the best schedule known.")
_A visualisation of the schedules being looked at in real-time, alongside statistics about the current best known schedule._

***

![alt text](https://github.com/liamtbrand/se306p1g8/wiki/BUCKETS.png "The final visualisation, grouping schedules into their respective lower bounds, to provide insights into the running of the algorithm.")
_A visualisation of the lower bounds of the algorithms searched through, ordered into buckets._

***






