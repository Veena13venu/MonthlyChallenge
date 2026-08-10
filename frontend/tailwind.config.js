/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{js,ts,jsx,tsx}'],
  theme: {
    extend: {
      colors: {
        primary: {
          50:  '#f0fdf4',
          100: '#dcfce7',
          500: '#22c55e',
          600: '#16a34a',
          700: '#15803d',
        },
        amber: {
          400: '#fbbf24',
          500: '#f59e0b',
        },
        // Status colours matching BRD (FR-15)
        success: '#22c55e',   // green  — Completed / SUCCESS day
        partial: '#f59e0b',   // amber  — Half-Completed / PARTIAL day
        missed:  '#ef4444',   // red    — Missed day
      },
    },
  },
  plugins: [],
}
