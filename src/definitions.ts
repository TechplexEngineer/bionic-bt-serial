
export type BTDevice = {name:string, macAddress:string, class:number}

export type ListenCallback = (device:BTDevice, err?: any) => void;
export type DiscoveryCallback = (device:BTDevice, err?:any) => void;

export interface PluginListenerHandle {
  /**
   * Removes the callback, further events will not trigger the callback.
   */
  remove(): Promise<void>;
}

export enum PluginEvents {
  discovered       = "discovered",
  discoveryState   = "discoveryState",
  rawData          = "rawData",
  connected        = "connected",
  connectionFailed = "connectionFailed",
  connectionLost   = "connectionLost",
}
export type EventCallback = (info:any, err?:any) => void;

export type RawDataResult = {bytes:number[], from:BTDevice};


export interface BluetoothSerialPlugin {


  addListener(eventName:PluginEvents, callback:EventCallback): Promise<PluginListenerHandle>;
  addListener(eventName:'discoveryState', callback:(result:{starting?:boolean, completed?:boolean})=> void): Promise<PluginListenerHandle>;
  addListener(eventName:'discovered', callback:(result:BTDevice)=> void): Promise<PluginListenerHandle>;
  addListener(eventName:'rawData', callback:(result:RawDataResult)=> void): Promise<PluginListenerHandle>;
  addListener(eventName:'connected', callback:(result:BTDevice)=> void): Promise<PluginListenerHandle>;
  addListener(eventName:'connectionFailed', callback:(result:BTDevice)=> void): Promise<PluginListenerHandle>;
  addListener(eventName:'connectionLost', callback:(result:BTDevice)=> void): Promise<PluginListenerHandle>;

  /**
   * Gets a list of the bonded (paired) devices.
   */
  getBondedDevices(): Promise<{ result: BTDevice[] }>;

  /**
   * Start listening for incoming connections
   */
  startListening(_options:{}, callback: ListenCallback): Promise<void>;

  /**
   * Stops listening for incoming connections.
   */
  stopListening(): Promise<void>;

  /**
   * True if listening for an accepting incoming connections.
   * A device acting as a server should be listening.
   */
  isListening(): Promise<{ result: boolean}>;

  /**
   * Connect to another device acting as a server that is in listening mode.
   * Should already be paired?
   */
  connect(options: { macAddress:string }): Promise<void>;

  /**
   * True if there is an active connection to the provided macAddress false otherwise.
   */
  isConnected(options: { macAddress:string }): Promise<{ result: boolean}>;

  /**
   * Gets a list of the connected devices.
   */
  getConnectedDevices(): Promise<{ result: BTDevice[] }>;

  /**
   * Disconnects specified connection.
   */
  disconnect(options: { macAddress:string }): Promise<{ result: boolean}>;

  /**
   * Disconnects all connections (incomming and outgoing).
   */
  disconnectAll(): Promise<void>;

  /**
   * Write data to specified macAddress
   */
  write(options: {macAddress:string, data:number[]}): Promise<{ result: boolean}>;

  /**
   * True if device has bluetooth enabled, false otherwise
   */
  isEnabled(): Promise<{result:boolean}>;

  /**
   * Prompt the user to enable bluetooth.
   * Resolved if bluetooth is enabled, rejects otherwise.
   */
  enableAdapter(): Promise<void>;

  /**
   * Prompt the user to enable bluetooth.
   * Resolved if bluetooth is enabled, rejects otherwise.
   */
  disableAdapter(): Promise<void>;

  /**
   * Open the bluetooth settings screen for the user.
   */
  showBluetoothSettings(): Promise<void>;

  /**
   * Starts discovery process, sends info about found devices to the callback.
   * Scans for about 12 seconds.
   */
  startDiscovery(_options:{}, callback: DiscoveryCallback): Promise<void>;

  /**
   * Stops any running discovery process.
   */
  cancelDiscovery(): Promise<void>;

  /**
   * Sets the name of the bluetooth adapter. Name is what paired devices will see when they connect.
   */
  setName(options: {name: string}): Promise<void>;

  /**
   * Gets the name of the bluetooth adapter.
   */
  getName(): Promise<{result: string}>;

  /**
   * Ensure bluetooth is enabled and the device is discoverable to remote scanners.
   * Default durationSec is 120 is not provided. Max is 300 seconds.
   */
  setDiscoverable(options: {durationSec?: number}): Promise<void>;

}
