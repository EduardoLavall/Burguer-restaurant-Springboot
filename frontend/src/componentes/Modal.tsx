import React from "react";
import { createPortal } from "react-dom";
import "./modal.css";

type ModalProps = {
  isOpen: boolean;
  onClose: () => void;
  title?: string;
  children: React.ReactNode;
};

export function Modal({ isOpen, onClose, title, children }: ModalProps) {
  if (!isOpen) return null;

  return createPortal(
    <div className="modal-overlay" onMouseDown={onClose}>
      <div className="modal" onMouseDown={(e) => e.stopPropagation()} role="dialog" aria-modal="true">
        <header className="modal__header">
          <h3 className="modal__titulo">{title}</h3>
          <button className="modal__fechar" onClick={onClose} aria-label="Fechar">X</button>
        </header>

        <div className="modal__body">{children}</div>
      </div>
    </div>,
    document.body
  );
}

export default Modal;
