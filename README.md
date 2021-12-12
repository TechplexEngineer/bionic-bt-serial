# bionic-bt-serial

Send data between bluetooth devices using a serial like interface

## Install

```bash
npm install bionic-bt-serial
npx cap sync
```

## API

<docgen-index>

* [`echo(...)`](#echo)
* [`getBondedDevices()`](#getbondeddevices)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### echo(...)

```typescript
echo(options: { value: string; }) => Promise<{ value: string; }>
```

| Param         | Type                            |
| ------------- | ------------------------------- |
| **`options`** | <code>{ value: string; }</code> |

**Returns:** <code>Promise&lt;{ value: string; }&gt;</code>

--------------------


### getBondedDevices()

```typescript
getBondedDevices() => Promise<{ devices: { name: string; address: string; id: string; class: number; }[]; }>
```

**Returns:** <code>Promise&lt;{ devices: { name: string; address: string; id: string; class: number; }[]; }&gt;</code>

--------------------

</docgen-api>
