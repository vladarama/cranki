// TodoList.tsx
import React, { useEffect, useState } from "react";
import TodoItem from "./TodoItem";

interface Todo {
  id: number;
  title: string;
  completed: boolean;
}

const TodoList: React.FC = () => {
  const [todos, setTodos] = useState<Todo[]>([]);

  // Fetch the initial list of todos
  useEffect(() => {
    fetch("/api/todoitems")
      .then((res) => res.json())
      .then((data) => setTodos(data));
  }, []);

  // Function to mark a task as done
  const markAsDone = async (id: number) => {
    try {
      const response = await fetch(`/api/todoitems/${id}/done`, {
        method: "PUT",
      });

      if (response.ok) {
        // Update the state with the completed task
        setTodos((prevTodos) =>
          prevTodos.map((todo) => (todo.id === id ? { ...todo, completed: true } : todo))
        );
      }
    } catch (error) {
      console.error("Error marking todo as done:", error);
    }
  };

  return (
    <div className="todo-list">
      {todos.map((todo) => (
        <TodoItem
          key={todo.id}
          id={todo.id}
          title={todo.title}
          completed={todo.completed}
          onComplete={markAsDone}
        />
      ))}
    </div>
  );
};

export default TodoList;
