export interface BluetoothSerialPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
}
