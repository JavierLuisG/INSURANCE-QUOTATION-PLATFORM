'use client';

// components/Feature/FeatureComponent.tsx — Plantilla de componente reutilizable

interface FeatureComponentProps {
  title?: string;
  onAction?: () => void;
  children?: React.ReactNode;
}

export default function FeatureComponent({ title, onAction, children }: FeatureComponentProps) {
  return (
    <div className="rounded-lg border border-gray-200 bg-white p-4 shadow-sm">
      {title && <h2 className="mb-3 text-lg font-semibold text-gray-900">{title}</h2>}
      <div className="text-gray-700">{children}</div>
      {onAction && (
        <button
          type="button"
          onClick={onAction}
          className="mt-4 rounded-md bg-blue-600 px-4 py-2 text-sm font-medium text-white hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500"
        >
          Acción
        </button>
      )}
    </div>
  );
}
