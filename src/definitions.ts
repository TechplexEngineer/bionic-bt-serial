
export type BTDevice = {name:string, address:string, id:string, class:number}

export type ListenCallback = (message: { connected:BTDevice }, err?: any) => void;
export type DiscoveryCallback = (message: { device:BTDevice }, err?:any) => void;


export interface BluetoothSerialPlugin {


  /**
   * Gets a list of the bonded (paired) devices.
   */
  getBondedDevices(): Promise<{ devices: BTDevice[] } | string>;

  /**
   * Start listening for incomming connections
   */
  startListening(callback: ListenCallback): Promise<void>;

  /**
   * Stops listening for incomming connections.
   */
  stopListening(): Promise<void>;

  /**
   * True if listening for an accepting incomming connections.
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
   * Disconnects all connections (incomming and outgoing).
   */
  disconnect(): Promise<void>;

  /**
   * Write data to specified macAddress
   *
   * @param      {{macAddress:string, data:object}}  options  The options
   * @return     {Promise}                          True if data successfully written
   */
  write(options: {macAddress:string, data:object}): Promise<{ result: boolean}>;

  /**
   * True if device has bluetooth enabled, false otherwise
   */
  isEnabled(): Promise<{result:boolean}>;

  /**
   * Prompt the user to enable bluetooth.
   * Resolved if bluetooth is enabled, rejects otherwise.
   */
  enable(): Promise<void>;

  /**
   * Open the bluetooth settings screen for the user.
   */
  showBluetoothSettings(): Promise<void>;

  /**
   * Starts discovery process, sends info about found devices to the callback.
   * Scans for about 12 seconds.
   */
  startDiscovery(callback: DiscoveryCallback): Promise<void>;

  /**
   * Stops any running discovery process.
   */
  cancelDiscovery(): Promise<void>;

  /**
   * Sets the nameov the bluetooth adapter. Name is what paired devices will see when they connect.
   */
  setName(options: {name: string}): Promise<void>;

  /**
   * Gets the name of the bluetooth adapter.
   */
  getName(): Promise<{name: string}>;

  /**
   * Ensure bluetooth is enabled and the device is discoverable to remote scanners.
   * Default durationSec is 120 is not provided. Max is 300 seconds.
   */
  setDiscoverable(options: {durationSec?: number}): Promise<void>;

}
