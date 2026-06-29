import { RouterProvider } from "@tanstack/react-router";

import { router } from "./rotas/router";

function App() {
  return <RouterProvider router={router} />;
}

export default App;