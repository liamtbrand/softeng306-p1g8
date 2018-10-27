---
layout: default
---

Winners of the Visualisation Contest, and Second Place in the Best Average Runtime, out of ~15/16 teams of 5, within the 2018 University of Auckland Software Engineering Part III cohort.

Our Team, TNTLER - Team Name is Trivial and Left as an Exercise for the Reader - presents **Tātai 8**.

![Figure 1](image1.png?raw=true)
Figure 1. Shows a visualisation of the search space of the algorithm, where the horizontal deviations refer to decisions made as to which tasks are processed when.

![Figure 2](image2.png?raw=true)
Figure 2. Shows a visualisation of the schedules being looked at in real-time, alongside statistics about the current best known schedule.

![Figure 3](image3.png?raw=true)
Figure 3. Shows a visualisation of the lower bounds of the algorithms searched through, ordered into buckets.

# About Tātai 8

This project is our implementation of the task scheduling system described by the SOFTENG 306 notes. Our implementation is a multi-algorithm approach; when visualisation is requested A* is used, branch and bound is used as a fall-back if the system runs out of memory. When visualisation is not needed or multiple threads are requested branch and bound is used, as on most graphs it is faster than our A* implementation.

The system is made up of multiple modules that can fit together to form the used algorithm. There are three heuristics, three child schedule generators, three storage systems and two algorithm implementations. This leads to a wide variety of customisation for the specific environment that the system is run in.

The current executable Jar runs using the combination of these modules that leads to the best combination of speed and memory usage.

# Thanks Team

A huge thanks goes to James Brown, Molly Farrant, Robert Rewcastle, Liam Brand, and Cyrus Raitava-Kumar for making this project happen.
