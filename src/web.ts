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
    "macAddress":"AA:BB:CC:DD:EE:FF",
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
    connections: {[key: string]: BTDevice} = {};
    adapterEnabled = false;

    async getBondedDevices(): Promise<{ result: BTDevice[] }> {
      return new Promise((resolve, _)=>{
        resolve({result: [getMockDevice(), getMockDevice()]})
      });
    }

    async startListening(_options:{}, callback: ListenCallback): Promise<void> {
      this.mIsListening = true;
      this.listeningHandle = setInterval(()=>{
        let dev = getMockDevice()
        this.connections[dev.macAddress] = dev;
        callback(dev)
      }, 2000)

      return new Promise((resolve,_)=>{
        resolve()
      })
    }

    async stopListening(): Promise<void> {
      this.mIsListening = false;
      if (this.listeningHandle != null) {
        clearInterval(this.listeningHandle);
        this.listeningHandle = null;
      }
      return new Promise((resolve,_)=>{
        resolve()
      })
    }

    async isListening(): Promise<{ result: boolean}> {
      return new Promise((resolve,_)=>{
        resolve({result: this.mIsListening})
      })
    }

    async connect(options: { macAddress:string }): Promise<void> {
      this.connections[options.macAddress] = {
        name: options.macAddress,
        macAddress: options.macAddress,
        "class": 524
      };
      return new Promise((resolve,_)=>{
        resolve()
      })
    }

    async isConnected(_options: { macAddress:string }): Promise<{ result: boolean}> {
      return new Promise((resolve,_)=>{
        resolve({result: true});
      })
    }

    async getConnectedDevices(): Promise<{ result: BTDevice[] }> {
      return new Promise((resolve,_)=>{
        resolve({result: Object.values(this.connections)});
      })
    }

    async disconnect(options: { macAddress:string }): Promise<{ result: boolean}> {
      delete this.connections[options.macAddress];
      return new Promise((resolve,_)=>{
        resolve({result: true})
      })
    }

    async disconnectAll(): Promise<void> {
      for (let macAddress in this.connections) {
        delete this.connections[macAddress];
      }
      return new Promise((resolve,_)=>{
        resolve()
      })
    }

    async write(_options: {macAddress:string, data:number[]}): Promise<{ result: boolean}> {
      return new Promise((resolve,_)=>{
        resolve({result: true})
      })
    }

    async isEnabled(): Promise<{result:boolean}> {
      return new Promise((resolve,_)=>{
        resolve({result: this.adapterEnabled})
      })
    }

    async enableAdapter(): Promise<void> {
      this.adapterEnabled = true;
      return new Promise((resolve,_)=>{
        resolve()
      })
    }
    async disableAdapter(): Promise<void> {
      this.adapterEnabled = false;
      return new Promise((resolve,_)=>{
        resolve()
      })
    }

    async showBluetoothSettings(): Promise<void> {
      return new Promise((resolve,_)=>{
        resolve()
      })
    }

    async startDiscovery(_options:{}, callback: DiscoveryCallback): Promise<void> {
      this.discoveryHandle = setInterval(()=>{
        callback(getMockDevice())
      }, 2000); // get a mock device every 2 seconds
      setTimeout(this.cancelDiscovery, 12*1000); // only discover for 12 seconds

      return new Promise((resolve,_)=>{
        resolve()
      })
    }

    async cancelDiscovery(): Promise<void> {
      return new Promise((resolve,_)=>{
        if (this.discoveryHandle != null) {
          clearInterval(this.discoveryHandle);
          this.discoveryHandle = null;
        }
        resolve()
      })
    }

    async setName(options: {name: string}): Promise<void> {
      this.adapterName = options.name;
      return new Promise((resolve,_)=>{
        resolve()
      })
    }

    async getName(): Promise<{result: string}> {
      return new Promise((resolve,_)=>{
        resolve({result: this.adapterName})
      })
    }

    async setDiscoverable(_options: {durationSec?: number}): Promise<void> {
      return new Promise((resolve,_)=>{
        resolve()
      })
    }

}


