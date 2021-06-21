package mx.ucol.models;

import java.util.ArrayList;

public class Todos {

    private ArrayList<Todo> todos = new ArrayList<Todo>();

    public void addToDo( Todo task) {
        this.todos.add(task);
    }

    public ArrayList<Todo> getTodos () {
        return this.todos;
    }
    //crear una clase que contenga varios todo

}
