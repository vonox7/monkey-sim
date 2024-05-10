# An artificial life simulator

Try a live demo at [vonox7.github.io/monkey-sim/](https://vonox7.github.io/monkey-sim/).

![Screenshot of the simulator's web interface](/static/screenshot-1.png)

<video alt="Short video demo of the application" src="/static/demo.mp4" controls="controls" style="max-width: 730px;"></video>

![Nighttime view of simulator](/static/screenshot-2.png)

💡 You might need a strong computer to run the whole simulation without frame drops. The JVM desktop application is faster, see building
section for instructions.

## Features

- A population of actors who live in a city and do their daily activities:
  - Work
  - Eat
  - Sleep
  - Socialize (by going to a club or visiting a friend)
  - Pursuing hobbies (by going to a park or gym)
  - Leisure time by watching TV at home
- Actors need to do their needs based on external and internal factors:
  - Time of the day:
    - Each place has a different opening time (depends on the type of place)
    - Each workplace has different working hours. Some have even core hours when employees _must_ work and hours when they
      _can_ work
    - Lunchtime is favoured by working actors between 12:00 and 14:00
    - Sleep time during the night. You can watch actors turn off the light when they go to sleep at night.
  - Day of the week: Education and work are only available on weekdays
  - Age: Actors have 4 stages of life: Toddler, Child, Adult, Senior.
    - Toddler (<6 years): Can only stay at home and eat, sleep and watch TV
    - Children (6-18 years): go to school and pursue daily life (except work)
    - Adults (6-70 years): work and pursue daily life
    - Seniors (70+): can retire from work and pursue daily life
- Ageing and reproduction:
  - Actors look for a partner (based on their gender preference and age)
  - Actors can have children (if they have a partner and are in a fertile age)
  - Actors age and eventually die, leaving their relatives or friends their inheritance
- Social connections:
  - Actors can have 0 or 1 partner
  - Actors can have 0 to 30 friends
  - Friends have a "connection strength" that can be increased by spending time together
  - Depending on which activity you do together, the connection strength increases more or less
  - The connection strength decreases over time
  - A high enough connection strength (>= 10) allows actors to become partners, if they fulfill all romantic
    requirements (age, gender, relationship status)
  - When people get into a relationship, they move in together
- Work:
  - Each adult actor can have a job
  - A job has a salary, working hours, and a workplace where actors need to commute to
  - Actors can be fired
  - Adult actors without a job look for new jobs by visiting workplaces and looking if their educational years match the
    workplace requirements
  - Actors look for a job that is close to their home, or where people they know work
- Education:
  - Children start their education at age 6
  - At age 18 actors still go to school, but parallel look for jobs
  - Actors can continue their education up to an age of 25
  - Each job has a certain education level requirement

All features can be observed in the simulation by looking at the city map, following individual actors or analyzing the
statistics.

The map is inspired by the city [Graz](https://wikipedia.org/wiki/Graz), Austria.
The distribution of areas like residential, industrial, parks and others is based on the real city.

## Building

This is a [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html) project
targeting Web via [Webassembly](https://kotl.in/wasm/) & Desktop.
The UI is built using [Compose Multiplatform](https://github.com/JetBrains/compose-multiplatform/#compose-multiplatform) and
the simulation uses a custom canvas renderer.

Run the JVM desktop application locally with `./gradlew desktopRun -DmainClass=MainKt --quiet`.
And the web application with `./gradlew :composeApp:wasmJsBrowserDevelopmentRun`.

# Authors

Created as part of a Hackathon in April 2024 by [Valentin](https://github.com/vonox7)
and [Filippo](https://filippo-orru.com).