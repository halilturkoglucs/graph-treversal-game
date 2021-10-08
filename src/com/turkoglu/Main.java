package com.turkoglu;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Main {

    // dimensions for the console
    private static int left = 1, right = 1, up = 1, down = 1;

    public static void main(String[] args) {
        Map<String, Room> dungeon = readFile();
        assert dungeon != null;

        String[][] dungeonMapForConsole = getDungeonMapForConsole(dungeon);

        play(dungeon, dungeonMapForConsole);
    }

    private static Map<String, Room> readFile() {
        File file = new File("src/com/turkoglu/lines.txt");

        //read file into stream, try-with-resources
        try (Stream<String> stream = Files.lines(file.getAbsoluteFile().toPath())) {
            Map<String, Room> rooms = new HashMap<>();

            stream.filter(line -> line.length() > 0).forEach(line -> {
                String currentRoomName = line.substring(0,2);
                Room currentRoom = rooms.getOrDefault(currentRoomName, new Room(currentRoomName));

                line = line.substring(3);

                String[] parts = line.split(" ");
                for (String edge : parts) {
                    String[] info = edge.split(":");
                    String direction = info[0];
                    String neighbourRoomName = info[1];

                    Room neighbour = rooms.get(neighbourRoomName);
                    if (neighbour == null) {
                        neighbour = new Room(neighbourRoomName);
                        rooms.put(neighbourRoomName, neighbour);
                    }

                    if ("e".equals(direction)) {
                        currentRoom.setE(neighbour);
                    } else if ("w".equals(direction)) {
                        currentRoom.setW(neighbour);
                    } else if ("n".equals(direction)) {
                        currentRoom.setN(neighbour);
                    } else {
                        currentRoom.setS(neighbour);
                    }

                    rooms.put(currentRoom.getName(), currentRoom);
                }
            });

            return rooms;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static String[][] getDungeonMapForConsole(Map<String, Room> dungeon) {
        int[] dimensions = extractDimensions(dungeon);
        System.out.println("Dungeon is of " + dimensions[0] + "x" + dimensions[1]);

        // initialise the dungeon map to be used during console output
        String[][] dungeonMapForConsole = new String[dimensions[0]][dimensions[1]];

        // fill with "-" initially - also to detect empty positions
        for (int i = 0; i < dimensions[0]; i++) {
            for (int j = 0; j < dimensions[1]; j++) {
                dungeonMapForConsole[i][j] = " ";
            }
        }

        return dungeonMapForConsole;
    }

    private static int[] extractDimensions(Map<String, Room> dungeon) {
        // Get random first position
        Room position = dungeon.get(dungeon.keySet().iterator().next());

        traverse(dungeon, position, 1, 1, new HashSet<>());

        int[] dimensions = new int[2];
        dimensions[0] = Math.abs(up - down) + 1;
        dimensions[1] = Math.abs(right - left) + 1; // max line length

        return dimensions;
    }

    /**
     * Row represents at which line we should print this room
     * Column represents, where in a line, we should print this room
     * ---
     *  b0
     *  a0
     *  c0
     * ---
     * here a0 has (1,1), b0 has (0,1), c0 has (2,1) relative to a0
     * @param position
     * @param row
     * @param column
     */
    private static void traverse(Map<String, Room> dungeon, Room position, int row, int column, Set<Room> visited) {
        if (position == null || visited.contains(position)) return;
        if (row < up) up = row;
        if (row > down) down = row;
        if (column < left) left = column;
        if (column > right) right = column;

        visited.add(position);

        // store position in the console output
        position.setRow(row);
        position.setCol(column);
        dungeon.put(position.getName(), position);

//        traverse(dungeon, position.getN(), row - 1, column, visited);
//        traverse(dungeon, position.getS(), row + 1, column, visited);
//        traverse(dungeon, position.getE(), row, column + 1, visited);
//        traverse(dungeon, position.getW(), row, column - 1, visited);

        traverse(dungeon, position.getN(), row - 2, column, visited);
        traverse(dungeon, position.getS(), row + 2, column, visited);
        traverse(dungeon, position.getE(), row, column + 2, visited);
        traverse(dungeon, position.getW(), row, column - 2, visited);

    }

    private static void play(Map<String, Room> dungeon, String[][] dungeonMapForConsole) {
        // Get random first position
        Room position = dungeon.get(dungeon.keySet().iterator().next());
        Scanner scanner = new Scanner(System.in);

        while (true) {
            printDashes(dungeonMapForConsole);
            System.out.println();
            printMap(dungeon, position, dungeonMapForConsole);
            printPossibleMoves(position);
            position = takeChoice(scanner, position);
            printDashes(dungeonMapForConsole);
        }
    }

    private static void printDashes(String[][] dungeonMapForConsole) {
        int width = dungeonMapForConsole[0].length;
        IntStream.rangeClosed(0, width).forEach(i -> System.out.print("-"));
    }

    private static void printMap(Map<String, Room> dungeon, Room position, String[][] dungeonMapForConsole) {
        dungeon.forEach((k, v) -> {
            int row = v.getRow(), col = v.getCol();
            row = Math.abs(up - row);
            col = Math.abs(left - col);

            // each room on a row takes up max 4 chars: -a0-
            // each room on a column takes up max 3 chars:
            // |
            // a0
            // |
            if (v != position) dungeonMapForConsole[row][col] = v.getName();
            else dungeonMapForConsole[row][col] = "**";

            if (v.getE() != null) {
                dungeonMapForConsole[row][col+1] = "-";
            }

            if (v.getW() != null) {
                dungeonMapForConsole[row][col-1] = "-";
            }

            if (v.getN() != null) {
                dungeonMapForConsole[row-1][col] = " | ";
            }

            if (v.getS() != null) {
                dungeonMapForConsole[row+1][col] = " | ";
            }
        });


        for (int i = 0; i < dungeonMapForConsole.length; i++) {
            for (int j = 0; j < dungeonMapForConsole[0].length; j++) {
                System.out.print(dungeonMapForConsole[i][j]);
            }
            System.out.println();
        }

        System.out.println();
    }

    private static void printPossibleMoves(Room position) {
        System.out.println("you are in room " + position.toString());
        System.out.print("possible moves: ");

        StringBuilder chocies = new StringBuilder();
        if (position.getE() != null) {
            chocies.append("e, ");
        }

        if (position.getW() != null) {
            chocies.append("w, ");
        }

        if (position.getN() != null) {
            chocies.append("n, ");
        }

        if (position.getS() != null) {
            chocies.append("s, ");
        }

        if (chocies.length() > 0) {
            System.out.print(chocies.substring(0, chocies.length() - 2));
        }

        System.out.println();
    }

    private static Room takeChoice(Scanner scanner, Room room) {
        System.out.print("your choice:");
        String choices = scanner.nextLine();
        Room current = room;

        for (char c : choices.toCharArray()) {
            if (c == 'n') {
                current = current.getN();
            } else if (c == 's') {
                current = current.getS();
            } else if (c == 'e') {
                current = current.getE();
            } else {
                current = current.getW();
            }

            if (current == null) {
                System.out.println("Invalid choice at : " + c);
                current = room;

                break;
            }
        }

        return current;
    }
}
