import React from "react";

interface TodoDetailViewProps {
  todo: {
    id: number;
    name: string;
    description: string;
    status: "NOT_DONE" | "DONE" | "IN_PROGRESS";
  };
  onClose: () => void;
}

const TodoDetailView: React.FC<TodoDetailViewProps> = ({ todo, onClose }) => {
  return (
    <div className="p-4">
      <button onClick={onClose} className="mb-4 text-red-500 hover:underline">
        Close
      </button>
      <h2 className="text-2xl font-bold mb-2">Todo Details</h2>
      <p>
        <strong>ID:</strong> {todo.id}
      </p>
      <p>
        <strong>Name:</strong> {todo.name}
      </p>
      <p>
        <strong>Status:</strong> {todo.status}
      </p>
      <p>
        <strong>Description:</strong> {todo.description}
      </p>
    </div>
  );
};

export default TodoDetailView;
