package com.turkoglu;

import java.util.Objects;

public class Room {
    String name;
    Room s, w, e, n;
    int row, col; // relative position on the console output to the first position

    public Room(String name) {this.name = name;}

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Room room = (Room) o;
        return name.equals(room.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Room getS() {
        return s;
    }

    public void setS(Room s) {
        this.s = s;
    }

    public Room getW() {
        return w;
    }

    public void setW(Room w) {
        this.w = w;
    }

    public Room getE() {
        return e;
    }

    public void setE(Room e) {
        this.e = e;
    }

    public Room getN() {
        return n;
    }

    public void setN(Room n) {
        this.n = n;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }
}
