package mx.ucol.handlers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import mx.ucol.models.Todo;
import mx.ucol.helpers.DBConnection;
import mx.ucol.helpers.JSON;
import mx.ucol.models.Todos;

public class TodosHandler implements HttpHandler {
    public void handle(HttpExchange exchange) throws IOException {
        String requestMethod = exchange.getRequestMethod();

        switch (requestMethod) {
            case "GET":
                getHandler(exchange);
                break;
            case "POST":
                postHandler(exchange);
                break;
            case "PUT":
                putHandler(exchange);
                break;
            case "DELETE":
                deleteHandler(exchange);
                break;
            default:
                notSupportedHandler(exchange);
                break;
        }
    }

    private void getHandler(HttpExchange exchange) throws IOException {

        /*
         * Supported endpoints for the GET handler: GET /todos Get all the ToDo entries
         * rom the DB instance
         *
         * GET /todos/:id Get the ToDo entry with the :id from the DB instance
         *
         * To get the connection to the DB use the following Connection connection =
         * DBConnection.getInstance();
         *
         * Then you can use the connection variable to create statements.
         */
        Connection db = DBConnection.getInstance();

        String qry = "";
        ResultSet rset = null;
        int tasks = 0;
        String res = null;
        int requestId  = -1;
        System.out.println(exchange.getRequestURI());
        if (exchange.getRequestURI().toString().equals("/api/v1/todos")){

            try {
                qry = "select * from todos";

                Statement stmt = db.createStatement();
                rset = stmt.executeQuery(qry);
                Todos todos = new Todos();

                while(rset.next()) {
                    int id = rset.getInt("id");
                    String title = rset.getString("title");
                    Boolean completed = rset.getBoolean("completed");
                    tasks++;
                    Todo todo = new Todo();
                    todo.setId(id);
                    todo.setTitle(title);
                    todo.setCompleted(completed);
                    System.out.println(completed);
                    todos.addToDo(todo);
                }
                res = JSON.objectToJson(todos);
                System.out.println("llego " + res);
            } catch ( SQLException e) {
                System.err.println();
            }

            OutputStream output = exchange.getResponseBody();

            byte[] response = res.getBytes();

            exchange.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
            exchange.sendResponseHeaders(200, response.length);
            output.write(response);
            output.close();
        } else {
            try {
                String strId = (exchange.getRequestURI().toString()).replace("/api/v1/todos", "").replace("/", "");
                System.out.println(strId);
                requestId = Integer.parseInt(strId);
            } catch (NumberFormatException e) {
                this.sendNotFoundEP(exchange);
            }

            if (requestId != -1) {
                try {
                    qry = "select * from todos where id=\"" + requestId + "\";";

                    Statement stmt = db.createStatement();
                    rset = stmt.executeQuery(qry);
                    Todos todos = new Todos();

                    if(rset.next()) {
                        int id = rset.getInt("id");
                        String title = rset.getString("title");
                        Boolean completed = rset.getBoolean("completed");
                        tasks++;
                        Todo todo = new Todo();
                        todo.setId(id);
                        todo.setTitle(title);
                        todo.setCompleted(completed);

                        res = JSON.objectToJson(todo);
                    } else {
                        res = requestId + " is not a valid Id";
                    }

                    System.out.println("llego " + res);
                } catch ( SQLException e) {
                    System.err.println();
                }

                OutputStream output = exchange.getResponseBody();

                byte[] response = res.getBytes();

                exchange.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
                exchange.sendResponseHeaders(404, response.length);
                output.write(response);
                output.close();
            }
        }
    }

    private void postHandler(HttpExchange exchange) throws IOException {

        /*
         * Supported endpoints for the POST handler: POST /todos Creates a new ToDo
         * entry
         *
         * You can use getBodyContent to read a JSON from the HTTP request: String
         * jsonBody = getBodyContent(exchange.getRequestBody())
         *
         * Try to convert the jsonBody variable to a Todo object to ensure the JSON is
         * well-formed.
         */

        String json = getBodyContent(exchange.getRequestBody());
        Todo task = null;
        Connection db = DBConnection.getInstance();
        String qry = null;

        try {
            task = JSON.jsonToObject(json);
            try {
                Statement stmt = db.createStatement();
                qry = "insert into todos (id, title, completed) values (" + task.getId() + ", \"" + task.getTitle() + "\", " + task.getCompleted() + ")" ;
                System.out.println(qry);

                stmt.executeUpdate(qry);
            } catch ( SQLException e) {
                System.err.println();
            }

            OutputStream output = exchange.getResponseBody();

            byte[] response = ("POST Handler a agregado el regsitro exitosamente:\n\n " + json).getBytes();

            exchange.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
            exchange.sendResponseHeaders(200, response.length);
            output.write(response);
            output.close();
        } catch (Exception e) {
            OutputStream output = exchange.getResponseBody();
            byte[] response = "JSON is not well formed".getBytes();

            exchange.sendResponseHeaders(400, response.length);
            output.write(response);
            output.close();
        }
    }

    private void putHandler(HttpExchange exchange) throws IOException {

        /*
         * Supported endpoints for the PUT handler POST /todos/:id Update the details of
         * ToDo entry with :id if exists
         */

        String json = getBodyContent(exchange.getRequestBody());
        Todo task = null;
        Connection db = DBConnection.getInstance();
        String qry = null;
        String res = null;
        int requestId = -1;

        try {
            task = JSON.jsonToObject(json);

            try {
                String strId = (exchange.getRequestURI().toString()).replace("/api/v1/todos", "").replace("/", "");
                System.out.println(strId);

                requestId = Integer.parseInt(strId);
                try {
                    Statement stmt = db.createStatement();
                    qry = "update todos set id=" + task.getId() + ", title=\"" + task.getTitle() + "\", completed=" + task.getCompleted() + " where id=" + requestId + ";";
                    System.out.println(qry);

                    res = stmt.executeUpdate(qry) != 0 ? "The update has been succesfully" : requestId + " is not a valid Id";
                } catch (SQLException e) {
                    System.err.println();
                }

                OutputStream output = exchange.getResponseBody();
                byte[] response = res.getBytes();

                exchange.sendResponseHeaders(200, response.length);
                output.write(response);
                output.close();
            } catch (NumberFormatException e) {
                this.sendNotFoundEP(exchange);
            }
        } catch (Exception e) {
            OutputStream output = exchange.getResponseBody();
            byte[] response = "JSON is not well formed".getBytes();

            exchange.sendResponseHeaders(400, response.length);
            output.write(response);
            output.close();
        }


    }

    private void deleteHandler(HttpExchange exchange) throws IOException {

        /*
         * Supported endpoints for the DELETE handler DELETE /todos/:id Remove ToDo
         * entry with :id if exists
         */

        Connection db = DBConnection.getInstance();
        String qry = null;
        String res = null;
        int requestId = -1;

        try {
            String strId = (exchange.getRequestURI().toString()).replace("/api/v1/todos", "").replace("/", "");
            System.out.println(strId);

            requestId = Integer.parseInt(strId);
            try {
                Statement stmt = db.createStatement();
                qry = "delete from todos where id=" + requestId + ";";
                System.out.println(qry);

                res = stmt.executeUpdate(qry) != 0 ? "The delete has been succesfully" : requestId + " is not a valid Id";
            } catch (SQLException e) {
                System.err.println();
            }


            OutputStream output = exchange.getResponseBody();
            byte[] response = res.getBytes();

            exchange.getResponseHeaders().set("Content-type", "application/json");
            exchange.sendResponseHeaders(200, response.length);
            output.write(response);
            output.close();
        } catch (NumberFormatException e) {
            this.sendNotFoundEP(exchange);
        }
    }

    private void notSupportedHandler(HttpExchange exchange) throws IOException {
        OutputStream output = exchange.getResponseBody();
        byte[] response = "Not supported".getBytes();

        exchange.sendResponseHeaders(200, response.length);
        output.write(response);
        output.close();
    }

    private String getBodyContent(InputStream input) throws UnsupportedEncodingException, IOException {
        InputStreamReader streamReader = new InputStreamReader(input, "utf-8");
        BufferedReader bufferedReader = new BufferedReader(streamReader);

        int buffer;
        StringBuilder builder = new StringBuilder();

        while ((buffer = bufferedReader.read()) != -1) {
            builder.append((char) buffer);
        }

        bufferedReader.close();
        streamReader.close();

        return builder.toString();
    }

    private void sendNotFoundEP (HttpExchange exchange) throws IOException {
        byte[] response = "API endpoint not found.".getBytes();
        exchange.sendResponseHeaders(404, response.length);

        OutputStream output = exchange.getResponseBody();
        output.write(response);
        output.close();
    }


}
