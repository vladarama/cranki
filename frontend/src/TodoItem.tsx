// TodoItem.tsx
import React from "react";

interface TodoItemProps {
  id: number;
  title: string;
  completed: boolean;
  onComplete: (id: number) => void;
}

const TodoItem: React.FC<TodoItemProps> = ({ id, title, completed, onComplete }) => {
  const handleCheckboxChange = () => {
    onComplete(id);
  };

  return (
    <div className={`todo-item ${completed ? "done" : ""}`}>
      <input type="checkbox" checked={completed} onChange={handleCheckboxChange} />
      <span className="todo-title">{title}</span>
    </div>
  );
};

export default TodoItem;
