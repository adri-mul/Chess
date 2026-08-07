# Java Chess

A simple chess application written in Java. This repository contains the source code for a playable chess game, including game logic, board representation, move validation, and a user interface (console or GUI depending on the project structure).

## Table of Contents
- About
- Features
- Requirements
- Building
- Running
- How to Play
- Project Structure
- Contributing
- License

## About

This project implements the rules of chess and provides a way to play games between two players (local). It aims to be a clean, well-documented implementation suitable for learning, experimentation, and extension (e.g., adding AI or network play).

## Features

- Full move generation and validation for all standard chess pieces
- Turn-based play for two players
- Check and checkmate detection
- Stalemate and basic draw conditions
- Board representation and move history

## Requirements

- Java Development Kit (JDK) 8 or newer

## Building

If, for whatever reason, rebuilding the project is necessary, follow these steps:

1. From the project root, compile with javac (example):

```sh
javac -d out $(find . -name "*.java")
```

2. Create a JAR file with the compiled files (Make sure Java is installed on your machine, as it is currently NOT bundled with the project)

```sh
jar cvfe example.jar adri.chess.ChessRunner -C bin .
```

3. When running, replace `chess.jar` with your choice of name (in this case, `example.jar`)

## Running

Run the JAR using

```sh
java -jar chess.jar
```

## How to Play

- Start the application and select the `Game` menu then the `Play` menu.
- Choose from a selection of different matchups.
- On your turn, select your piece that you want to move. The game will automatically highlight squares you can move to with a translucent dot, and squares you can capture on with a purple highlight. To turn this off, choose `Options` > `Hide Legal Moves`.
- Moves are logged on the right-hand-side panel, and follows algebraic chess notation.

## Project Structure

- src/ - Java source files
- bin/ - Class files
- src/adri/chess/images - Image resources

## License

This project uses a MIT license. Please see LICENSE for more details.