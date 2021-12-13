import { WebPlugin } from '@capacitor/core';

import type {
  BluetoothSerialPlugin,
  ListenCallback,
  DiscoveryCallback,
  BTDevice
} from './definitions';

function getRandomInt(min:number, max:number): number {
  min = Math.ceil(min);
  max = Math.floor(max);
  return Math.floor(Math.random() * (max - min) + min); //The maximum is exclusive and the minimum is inclusive
}

function getMockDevice(): BTDevice {
  return {
    "name":`DeviceName ${getRandomInt(1, 10)}`,
    "address":"AA:BB:CC:DD:EE:FF",
    "id":"AA:BB:CC:DD:EE:FF",
    "class":524
  };
}

export class BluetoothSerialWeb
  extends WebPlugin
  implements BluetoothSerialPlugin {

    discoveryHandle: ReturnType<typeof setInterval>|null = null;
    listeningHandle: ReturnType<typeof setInterval>|null = null;
    adapterName = "default adapter name";
    mIsListening = false;

    async echo(options: { value: string }): Promise<{ value: string }> {
      console.log('echo', options);
      return options;
    }

    async getBondedDevices(): Promise<{ devices: {name:string, address:string, id:string, class:number}[] }> {
      console.log('getBondedDevices');
      return new Promise((resolve, _)=>{
        resolve({devices: [getMockDevice(), getMockDevice()]})
      });
    }

    async startListening(callback: ListenCallback): Promise<void> {
      console.log("startListening");
      this.mIsListening = true;
      this.listeningHandle = setInterval(()=>{
        callback({connected: getMockDevice()})
      }, 2000)

      return new Promise((resolve,_)=>{
        resolve()
      })
    }

    async stopListening(): Promise<void> {
      console.log("stopListening");
      if (this.listeningHandle != null) {
        clearInterval(this.listeningHandle);
        this.listeningHandle = null;
      }
      return new Promise((resolve,_)=>{
        resolve()
      })
    }

    async isListening(): Promise<{ result: boolean}> {
      console.log("isListening");
      return new Promise((resolve,_)=>{
        resolve({result: this.mIsListening})
      })
    }

    async connect(options: { macAddress:string }): Promise<void> {
      console.log(`connect to ${options.macAddress}`);
      return new Promise((resolve,_)=>{
        resolve()
      })
    }

    async isConnected(options: { macAddress:string }): Promise<{ result: boolean}> {
      console.log(`isConnected to ${options.macAddress}`);
      return new Promise((resolve,_)=>{
        resolve({result: true});
      })
    }

    async disconnect(): Promise<void> {
      console.log("disconnect");
      return new Promise((resolve,_)=>{
        resolve()
      })
    }

    async write(options: {macAddress:string, data:object}): Promise<{ result: boolean}> {
      console.log(`write to ${options.macAddress} data ${options.data}`);
      return new Promise((resolve,_)=>{
        resolve({result: true})
      })
    }

    async isEnabled(): Promise<{result:boolean}> {
      console.log("isEnabled");
      return new Promise((resolve,_)=>{
        resolve({result: true})
      })
    }

    async enable(): Promise<void> {
      console.log("enable");
      return new Promise((resolve,_)=>{
        resolve()
      })
    }

    async showBluetoothSettings(): Promise<void> {
      console.log("showBluetoothSettings");
      return new Promise((resolve,_)=>{
        resolve()
      })
    }

    async startDiscovery(callback: DiscoveryCallback): Promise<void> {
      console.log("startDiscovery");
      this.discoveryHandle = setInterval(()=>{
        callback({device: getMockDevice()})
      }, 2000); // get a mock device every 2 seconds
      setTimeout(this.cancelDiscovery, 12*1000); // only discover for 12 seconds

      return new Promise((resolve,_)=>{
        resolve()
      })
    }

    async cancelDiscovery(): Promise<void> {
      console.log("cancelDiscovery");
      return new Promise((resolve,_)=>{
        if (this.discoveryHandle != null) {
          clearInterval(this.discoveryHandle);
          this.discoveryHandle = null;
        }
        resolve()
      })
    }

    async setName(options: {name: string}): Promise<void> {
      console.log("setName");
      this.adapterName = options.name;
      return new Promise((resolve,_)=>{
        resolve()
      })
    }

    async getName(): Promise<{name: string}> {
      console.log("getName");
      return new Promise((resolve,_)=>{
        resolve({name: this.adapterName})
      })
    }

    async setDiscoverable(options: {durationSec?: number}): Promise<void> {
      console.log(`setDiscoverable for ${options.durationSec||120} seconds`);
      return new Promise((resolve,_)=>{
        resolve()
      })
    }

}


