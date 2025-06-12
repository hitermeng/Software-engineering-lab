import { Options, PublicPluginAPI } from "./types-BOtuQ8Dm.js";
import { Plugin } from "vite";

//#region src/vite.d.ts
declare const _default: (options?: Options | undefined) => Plugin & {
  api: PublicPluginAPI;
};

//#endregion
export { _default as default };