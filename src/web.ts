import { WebPlugin } from '@capacitor/core';

import type { BluetoothSerialPlugin } from './definitions';

export class BluetoothSerialWeb
  extends WebPlugin
  implements BluetoothSerialPlugin {
  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }
}
