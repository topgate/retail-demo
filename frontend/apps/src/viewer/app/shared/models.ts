export interface Receipt {
    price: number;
    productCode: string;
    productName: string;
    quantity: number;
    receiptBranchNumber: number;
    receiptNumber: number;
    registerID: string;
    salesDate: number;
    storeCode: string;
    storeName: string;
}

export interface ReceiptWithKey extends Receipt {
  $key: string;
}

