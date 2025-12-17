"use client";

import { useSession, signIn } from "next-auth/react";
import { useState } from "react";
import Link from "next/link";
import UserDropdown from "./UserDropdown";

export default function Header() {
  const { data: session, status } = useSession();
  const [isSigningIn, setIsSigningIn] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleSignIn = async () => {
    try {
      setIsSigningIn(true);
      setError(null);
      await signIn("keycloak", { callbackUrl: "/" });
    } catch (err) {
      console.error("Sign in error:", err);
      setError("Failed to sign in. Please try again.");
      setIsSigningIn(false);
    }
  };

  return (
    <header className="sticky top-0 z-40 w-full border-b-2 border-slate-900 bg-black/95 backdrop-blur supports-[backdrop-filter]:bg-black/90">
      <div className="container mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex h-16 items-center justify-between">
          {/* Logo */}
          <div className="flex items-center">
            <Link href="/">
              <h1 className="text-xl font-bold text-white tracking-tight cursor-pointer hover:text-orange-500 transition-colors duration-200">
              AI Flashcards
            </h1>
            </Link>
          </div>

          {/* Auth Section */}
          <div className="flex items-center">
            {status === "loading" ? (
              <div className="flex items-center gap-2">
                <div className="w-8 h-8 bg-slate-900 border-2 border-slate-800 animate-pulse" />
                <div className="hidden sm:block w-20 h-4 bg-slate-900 border-2 border-slate-800 animate-pulse" />
              </div>
            ) : session ? (
              <UserDropdown session={session} />
            ) : (
              <div className="flex flex-col items-end gap-1">
                <button
                  onClick={handleSignIn}
                  disabled={isSigningIn}
                  className="px-6 py-2.5 bg-transparent border-2 border-orange-500 text-orange-500 hover:bg-orange-500 hover:text-black disabled:border-orange-800 disabled:text-orange-800 disabled:cursor-not-allowed font-semibold transition-all duration-200 focus:outline-none focus:ring-2 focus:ring-orange-500 focus:ring-offset-2 focus:ring-offset-black flex items-center gap-2 uppercase tracking-wide text-sm"
                  aria-label="Sign in"
                >
                  {isSigningIn ? (
                    <>
                      <div className="w-4 h-4 border-2 border-orange-500 border-t-transparent animate-spin" />
                      <span>Signing in...</span>
                    </>
                  ) : (
                    "Sign In"
                  )}
                </button>
                {error && (
                  <p className="text-xs text-red-400 max-w-[200px] text-right">
                    {error}
                  </p>
                )}
              </div>
            )}
          </div>
        </div>
      </div>
    </header>
  );
}
