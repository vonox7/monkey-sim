package definitions

object ActorNames {
  val firstNames = mapOf(
    Gender.Male to listOf(
      "James",
      "John",
      "Robert",
      "Michael",
      "William",
      "David",
      "Richard",
      "Joseph",
      "Thomas",
      "Charles",
      "Daniel",
      "Matthew",
      "Anthony",
      "Donald",
      "Mark",
      "Paul",
      "Steven",
      "Andrew",
      "Kenneth",
      "Joshua",
      "George",
      "Kevin",
      "Brian",
      "Edward",
      "Ronald",
      "Timothy",
      // German names
      "Peter",
      "Andreas",
      "Stefan",
      "Christian",
      "Markus",
      "Martin",
      "Alexander",
      "Patrick",
      "Sebastian",
      "Sascha",
      "Jens",
      "Frank",
      "Oliver",
      "Marco",
      "Sven",
      "Philipp",
      "Matthias",
      "Tobias",
      "Dirk",
      "Florian",
      "Jörg",
      "Kai",
      // Austrian names
      "Franz",
      "Johann",
      "Wolfgang",
      "Karl",
      "Josef",
      "Gerhard",
      "Heinz",
      "Werner",
      "Friedrich",
      "Georg",
      // Italian names
      "Mario",
      "Luigi",
      "Giovanni",
      "Giuseppe",
      "Antonio",
      "Angelo",
      "Francesco",
      "Vincenzo",
      "Pietro",
      "Salvatore",
      "Domenico",
      "Pasquale",
      "Gennaro",
      "Giorgio",
      "Giulio",
      "Giuliano",
      "Graziano",
      "Guido",
      "Gustavo",
      "Iacopo",
    ),
    Gender.Female to listOf(
      "Mary",
      "Patricia",
      "Jennifer",
      "Linda",
      "Elizabeth",
      "Barbara",
      "Susan",
      "Jessica",
      "Sarah",
      "Karen",
      "Nancy",
      "Lisa",
      "Betty",
      "Dorothy",
      "Sandra",
      "Ashley",
      "Kimberly",
      "Donna",
      "Emily",
      "Michelle",
      "Carol",
      "Amanda",
      "Melissa",
      "Deborah",
      "Stephanie",
      // German names
      "Sabine",
      "Andrea",
      "Petra",
      "Susanne",
      "Nicole",
      "Monika",
      "Kerstin",
      "Heike",
      "Katrin",
      "Anja",
      "Birgit",
      "Nadine",
      "Martina",
      "Julia",
      "Kathrin",
      "Jana",
      "Stefanie",
      "Tanja",
      "Nina",
      "Carina",
      "Jenny",
      "Laura",
      "Vanessa",
      "Christina",
      // Austrian names
      "Maria",
      "Anna",
      "Christine",
      "Elisabeth",
      "Helga",
      "Eva",
      "Brigitte",
      "Margarete",
      "Ingrid",
      "Renate",
      "Waltraud",
      "Gertrude",
      "Gabriele",
      "Hildegard",
      "Karin",
      "Irmgard",
      "Elfriede",
      "Roswitha",
      "Ursula",
      "Silvia",
      "Edith",
      "Rosa",
      // Italian names
      "Giuseppina",
      "Angela",
      "Giovanna",
      "Teresa",
      "Lucia",
      "Carmela",
      "Antonietta",
      "Francesca",
      "Luisa",
      "Carmen"
    ),
    Gender.Other to listOf(
      "Alex", "Ali",
      // German unisex names
      "Chris", "Dominique", "Elli", "Jule", "Kai", "Lauri", "Mika", "Niki", "Olli", "Pia",
      // Austrian unisex names
      "Dani", "Eli", "Franzi", "Hanni",
      // Italian unisex names
      "Andrea", "Daniele", "Franco", "Michele", "Toni",
    )
  )

  val lastNames = listOf(
    // English last names
    "Smith",
    "Johnson",
    "Williams",
    "Jones",
    "Brown",
    "Davis",
    "Miller",
    "Wilson",
    "Moore",
    "Taylor",
    "Anderson",
    "Thomas",
    "Jackson",
    "White",
    "Harris",
    "Martin",
    "Thompson",
    "Garcia",
    "Martinez",
    "Robinson",
    "Clark",
    "Rodriguez",
    "Lewis",
    "Lee",
    "Walker",
    "Hall",
    "Allen",
    "Young",
    "Hernandez",
    "King",
    "Wright",
    "Lopez",
    "Hill",
    "Scott",
    "Green",
    "Adams",
    "Baker",
    "Gonzalez",
    "Nelson",
    "Carter",
    "Mitchell",
    "Perez",
    "Roberts",
    "Turner",
    "Phillips",
    "Campbell",
    "Parker",
    "Evans",
    "Edwards",
    "Collins",
    "Stewart",
    "Sanchez",
    "Morris",
    "Rogers",
    "Reed",
    "Cook",
    "Morgan",
    "Bell",
    "Murphy",
    "Bailey",
    "Rivera",
    "Cooper",
    "Richardson",
    "Cox",
    "Howard",
    "Ward",
    "Torres",
    "Peterson",
    "Gray",
    "Ramirez",
    "James",
    "Watson",
    "Brooks",
    "Kelly",
    "Sanders",
    "Price",
    "Bennett",
    "Wood",
    "Barnes",
    "Ross",
    "Henderson",
    "Coleman",
    "Jenkins",
    "Perry",
    "Powell",
    "Long",
    "Patterson",
    "Hughes",
    "Flores",
    "Washington",
    "Butler",
    // Now some German names
    "Müller",
    "Schmidt",
    "Schneider",
    "Fischer",
    "Weber",
    "Meyer",
    "Wagner",
    "Becker",
    "Schulz",
    "Hoffmann",
    "Schäfer",
    "Koch",
    "Bauer",
    "Richter",
    "Klein",
    "Wolf",
    "Schröder",
    "Neumann",
    "Schwarz",
    "Zimmermann",
    "Braun",
    "Krüger",
    "Hofmann",
    "Hartmann",
    "Lange",
    "Schmitt",
    "Werner",
    "Schmitz",
    "Krause",
    "Meier",
    "Lehmann",
    "Schulze",
    "Maier",
    "Köhler",
    "Herrmann",
    "König",
    "Walter",
    "Mayer",
    "Huber",
    "Kaiser",
    "Fuchs",
    "Peters",
    "Lang",
    "Scholz",
    "Möller",
    "Weiß",
    "Jung",
    "Hahn",
    "Schubert",
    "Vogel",
    "Friedrich",
    "Keller",
    "Günther",
    "Frank",
    "Berger",
    "Winkler",
    "Roth",
    "Beck",
    "Lorenz",
    "Baumann",
    "Franke",
    "Albrecht",
    // Now some Italian ones that can not be used as firstNames
    "Rossi",
    "Russo",
    "Ferrari",
    "Esposito",
    "Bianchi",
    "Romano",
    "Colombo",
    "Ricci",
    "Marino",
    "Greco",
    "Bruno",
    "Gallo",
    "Conti",
    "De Luca",
    "Mancini",
    "Costa",
    "Giordano",
    "Rizzo",
    "Lombardi",
    "Moretti",
    "Barbieri",
    "Fontana",
    "Santoro",
    "Mariani",
    "Rinaldi",
    "Caruso",
    "Ferrara",
    "Galli",
    "Martini",
    "Leone",
    "Longo",
    "Gentile",
    "Martinelli",
    "Vitale",
    "Lombardo",
    "Serra",
    "Coppola",
    "De Santis",
    "D'Angelo",
    "Marchetti",
    "Parisi",
    "Villa",
    "Conte",
    "Ferraro",
    "Ferri",
    "Pellegrini",
    "Bellini",
    "Basile",
    "Rizzi",
    "De Angelis",
    "Palmieri",
    "Donati",
    "Orlando",
    "Negri",
    "Gatti",
    "Sala",
    "Silvestri",
    "Riva",
    "Marini",
    "Bernardi",
    // Now some Austrian ones that are not German
    "Gruber",
    "Pichler",
    "Steiner",
    "Moser",
    "Eder",
    "Leitner",
    "Hofer",
    "Wimmer",
    "Reiter",
    "Mayr",
    "Schweiger",
    "Haas",
    "Schuster",
    "Schweitzer",
    "Lechner",
    "Binder",
    "Wieser",
    "Ebner",
    "Schlager",
    "Koller",
  )
}