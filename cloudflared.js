import { bin, install } from "cloudflared";
import fs from "node:fs";
import { spawn } from "node:child_process";
import dotenv from "dotenv";

// load .env
dotenv.config();

const HOSTNAME = process.env.HOSTNAME || "remdb.aeoc.io.vn";
const PORT = process.env.PORT || "5432";
const LOCAL_HOST = process.env.LOCAL_HOST || "127.0.0.1";

if (!fs.existsSync(bin)) {
    console.log("Cloudflared binary not found, installing...");
    await install(bin);
    console.log("Cloudflared installed at:", bin);
}

const child = spawn(bin, [
    "access",
    "tcp",
    "--hostname", HOSTNAME,
    "--url", `${LOCAL_HOST}:${PORT}`
], { stdio: "inherit", shell: process.platform === "win32" });

child.on("exit", (code) => process.exit(code));
child.on("error", (err) => {
    console.error("Failed to start cloudflared:", err.message || err);
    process.exit(1);
});
