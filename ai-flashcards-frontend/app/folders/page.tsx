import { Suspense } from "react";
import Header from "@/components/Header";
import Footer from "@/components/Footer";
import Folders from "@/components/Folders";

export default function FoldersPage() {
  return (
    <div className="flex flex-col min-h-screen">
      <Header />
      <Suspense fallback={<div className="flex-1" />}>
        <Folders />
      </Suspense>
      <Footer />
    </div>
  );
}

