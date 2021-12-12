export interface BluetoothSerialPlugin {

  echo(options: { value: string }): Promise<{ value: string }>;

  getBondedDevices(): Promise<{ devices: {name:string, address:string, id:string, class:number}[] }>;
}
