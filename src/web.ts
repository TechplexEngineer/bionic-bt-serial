import { WebPlugin } from '@capacitor/core';

import type { BluetoothSerialPlugin } from './definitions';

export class BluetoothSerialWeb
  extends WebPlugin
  implements BluetoothSerialPlugin {

    async echo(options: { value: string }): Promise<{ value: string }> {
      console.log('echo', options);
      return options;
    }

    async getBondedDevices(): Promise<{ devices: {name:string, address:string, id:string, class:number}[] }> {
      console.log('getBondedDevices');
      return new Promise((resolve, _)=>{
        resolve({devices: [{"name":"DeviceName","address":"AA:BB:CC:DD:EE:FF","id":"AA:BB:CC:DD:EE:FF","class":524}]})
      });
    }
}


