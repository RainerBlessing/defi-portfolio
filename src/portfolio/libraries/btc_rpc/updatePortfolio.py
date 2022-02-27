import csv
import sys

from bitcoinrpc.authproxy import AuthServiceProxy
from pathlib import Path
import pandas as pd
import os
import numpy as np
import warnings
import platform
import pathlib
import requests
import time
import datetime

warnings.simplefilter(action='ignore', category=FutureWarning)
pd.options.mode.chained_assignment = None

if __name__ == '__main__':

    if False:
        if platform.system() == 'Linux' or platform.system() == 'Windows':
            pathPortfolioData = sys.argv[1]
            pathConfig = sys.argv[2]
        else:
            pathPortfolioData = sys.argv[0]
            pathConfig = sys.argv[0]
            pathPortfolioData = pathPortfolioData.replace("/updatePortfolio", "")  # match mac os path
            pathConfig = pathPortfolioData.replace("/updatePortfolio", "") + "/defi.conf"  # match mac os path
    pathPortfolioData = os.environ.get("APPDATA") + '\\defi-portfolio'
    pathConfig = 'C:\\Users\\Arthur\\Desktop\\defiJava\\PortfolioData\\defi.conf'

   # pathPortfolioData = "C:\\Users\\danie\\AppData\\Roaming\\defi-portfolio"
    # Addresse
    addresses = pd.read_csv(open(pathPortfolioData + '/Addresses.csv'), header = None)
#################
    local = 0

    if local == 0:
        data = pd.DataFrame()
        allRewards = []
        allTransactions1 = []
        allTransactions2 = []
        allTransactions3 = []
        for iAddress in range(0, addresses.__len__()):
            rewards = pd.read_json(requests.get("https://api.dfi.tax/p01/rwd/"+addresses.at[iAddress, 0]+"/day/EUR").content)
            rewards['address'] = addresses.at[iAddress, 0]
            transactions1 = pd.read_json(requests.get("https://api.dfi.tax/v01/hst/"+addresses.at[iAddress, 0]+"/2022/EUR").content)
            transactions1['address'] = addresses.at[iAddress, 0]
            transactions2 = pd.read_json(requests.get("https://api.dfi.tax/v01/hst/" + addresses.at[iAddress, 0] + "/2021/EUR").content)
            transactions2['address'] = addresses.at[iAddress, 0]
            transactions3 = pd.read_json(requests.get("https://api.dfi.tax/v01/hst/" + addresses.at[iAddress, 0] + "/2020/EUR").content)
            transactions3['address'] = addresses.at[iAddress, 0]

            if allRewards.__len__() == 0:
                allRewards = rewards
            else:
                allRewards = allRewards.append(rewards)
            if allTransactions1.__len__() == 0:
                allTransactions1 = transactions1
            else:
                allTransactions1 = allTransactions1.append(tansactions1)
            if allTransactions2.__len__() == 0:
                allTransactions2 = transactions2
            else:
                allTransactions2 = allTransactions2.append(transactions2)
            if allTransactions3.__len__() == 0:
                allTransactions3 = transactions3
            else:
                allTransactions3 = allTransactions3.append(transactions3)
    else:
        data = pd.read_json('C:/Users/Arthur/Desktop/test.json')


    # rewards aufbereiten
    transactions12 = []
    transactions12 = allTransactions1.append(allTransactions2)
    transactions123 = []
    transactions123 = transactions12.append(allTransactions3)
    transactions123.reset_index(inplace=True, drop=True)

    rewards['date'] = pd.DatetimeIndex(rewards['date']).astype(np.int64) / 1000000
    rewards['Amount'] = rewards['qty'].astype(str)+'@'+rewards['token']

    rewards = rewards.drop(['token','qty','value_open','value_close','cur_code'],axis=1)
    rewards['empty1'] = '_'
    rewards['empty2'] = '_'
    rewards['empty3'] = '_'
    rewards['empty4'] = '_'
    rewards = rewards[['date', 'address', 'category', 'Amount','empty1','empty2','pool','empty3','empty4']]
    rewards = rewards.rename(columns={'date': 'blockTime','address': 'owner','category': 'type','Amount': 'amounts','empty1': 'blockHash','empty2': 'blockHeight','pool': 'poolID','empty3': 'txid','empty4': 'rewardType'})
    rewards['blockHeight'] = 0

    # Transactions aufbereiten
    transactions = transactions123
    transactions['dt'] = pd.DatetimeIndex(transactions['dt']).astype(np.int64) / 1000000
    transactions = transactions.drop(['value'], axis=1)
    transactions['empty1'] = '_'
    transactions['empty2'] = '_'
    transactions['empty3'] = '_'
    transactions = transactions[['dt', 'address', 'cat', 'tokens', 'tx_id', 'blk_id', 'empty1', 'empty2', 'empty3']]
    transactions = transactions.rename(columns={'dt': 'blockTime', 'address': 'owner', 'cat': 'type', 'tokens': 'amounts',
                                      'tx_id': 'blockHash', 'blk_id': 'blockHeight', 'empty1': 'poolID',
                                      'empty2': 'txid', 'empty3': 'rewardType'})

    #
    swap = transactions.loc[transactions['type'] == 'PoolSwap']
    swap.reset_index(inplace=True, drop=True)
    transactions.drop(transactions.loc[transactions['type'] == 'PoolSwap'].index, inplace=True)
    addPool = transactions.loc[transactions['type'] == 'AddPoolLiquidity']
    transactions.drop(transactions.loc[transactions['type'] == 'AddPoolLiquidity'].index, inplace=True)
    removePool = transactions.loc[transactions['type'] == 'RemovePoolLiquidity']
    transactions.drop(transactions.loc[transactions['type'] == 'RemovePoolLiquidity'].index, inplace=True)

    splittedTrans = pd.DataFrame()
    for itrans in range(0, transactions.__len__()):
        row = transactions.iloc[itrans]
        first = transactions.iloc[itrans]['amounts'][0]
        row['amounts'] = str(first['qty']) + '@' + first['code']
        splittedTrans = splittedTrans.append(row)


    splittedSwaps = pd.DataFrame()
    for iswaps in range(0, swap.__len__()):
        row = swap.iloc[iswaps]
        first = swap.iloc[iswaps]['amounts'][0]
        second = swap.iloc[iswaps]['amounts'][1]
        row['amounts'] = str(first['qty']) + '@' + first['code']
        splittedSwaps = splittedSwaps.append(row)
        row['amounts'] = str(second['qty']) + '@' + second['code']
        splittedSwaps = splittedSwaps.append(row)

    splittedAddPool = pd.DataFrame()
    for iaddPool in range(0, addPool.__len__()):
        row = addPool.iloc[iaddPool]
        first = addPool.iloc[iaddPool]['amounts'][0]
        second = addPool.iloc[iaddPool]['amounts'][1]
        third = addPool.iloc[iaddPool]['amounts'][2]

        row['amounts'] = str(first['qty']) + '@' + first['code']
        splittedAddPool = splittedAddPool.append(row)
        row['amounts'] = str(second['qty']) + '@' + second['code']
        splittedAddPool = splittedAddPool.append(row)
        row['amounts'] = str(third['qty']) + '@' + third['code']
        splittedAddPool = splittedAddPool.append(row)

    splittedRemovePool = pd.DataFrame()
    for iremovePool in range(0, removePool.__len__()):
        row = removePool.iloc[iremovePool]
        first = removePool.iloc[iremovePool]['amounts'][0]
        second = removePool.iloc[iremovePool]['amounts'][1]
        third = removePool.iloc[iremovePool]['amounts'][2]

        row['amounts'] = str(first['qty']) + '@' + first['code']
        splittedRemovePool = splittedRemovePool.append(row)
        row['amounts'] = str(second['qty']) + '@' + second['code']
        splittedRemovePool = splittedRemovePool.append(row)
        row['amounts'] = str(third['qty']) + '@' + third['code']
        splittedRemovePool = splittedRemovePool.append(row)

    transactions = splittedTrans
    transactions = transactions.append(splittedSwaps)
    transactions = transactions.append(splittedAddPool)
    transactions = transactions.append(splittedRemovePool)
    transactions = transactions.astype({'blockHeight': 'int64'})


    transactions = transactions.append(rewards)
    transactions.reset_index(inplace=True, drop=True)

    transactions = transactions.sort_values(by=['blockTime'], ascending=True)
    transactions.reset_index(inplace=True, drop=True)
    transactions['blockTime'] = transactions['blockTime']/1000
    transactions = transactions.astype({'blockTime': 'int64'})

    # add to transactio.portfolio
    transactions.to_csv(pathPortfolioData + '/transactionData.portfolio', mode='a', header=False, sep=';', index=False)



    # # Aufsplittung add/remove Pool und poolswap
    # if data.__len__() != 0:
    #     swap = data.loc[data['type'] == 'PoolSwap']
    #     data.drop(data.loc[data['type'] == 'PoolSwap'].index, inplace=True)
    #     addPool = data.loc[data['type'] == 'AddPoolLiquidity']
    #     data.drop(data.loc[data['type'] == 'AddPoolLiquidity'].index, inplace=True)
    #     removePool = data.loc[data['type'] == 'RemovePoolLiquidity']
    #     data.drop(data.loc[data['type'] == 'RemovePoolLiquidity'].index, inplace=True)
    #     swapAdd = swap.append(addPool)
    #     swapAddRemove = swapAdd.append(removePool)
    #     swapAddRemoveSingles = pd.DataFrame()
    #     for i in range(0,swapAddRemove.__len__()):
    #         amounts = swapAddRemove.iloc[i]['amounts']
    #         for iAmount in range(0,amounts.__len__()):
    #             swapAddRemove.iloc[i,7]=amounts[iAmount]
    #             swapAddRemoveSingles = swapAddRemoveSingles.append(swapAddRemove.iloc[i])
    #     data = data.append(swapAddRemoveSingles)
    #
    #     data['blockTime'] = data['blockTime'].astype(int)
    #     data['blockHeight'] = data['blockHeight'].astype(int)
    #
    #
    #     # Aufsplittung dfi und betrag
    #     splittedAmount = []
    #     splittedCoin = []
    #   #  rawDataAmount = []
    #     rawData = data
    #     for i in data['amounts']:
    #         if isinstance(i, list):
    #             splitted = i[0].split('@')
    #         else:
    #             splitted = i.split('@')
    #         splittedAmount.append(float(splitted[0]))
    #         splittedCoin.append(splitted[1])
    #
    #
    #     data.insert(len(data.columns), 'Amount', splittedAmount)
    #     data.insert(len(data.columns), 'Coin', splittedCoin)
    #     data=data.drop(columns='amounts')
    #
    #
    #     # split dataFrame into rewards & commissions  and  rest
    #     dataRewCom = data.query('type == "Rewards" or type == "Commission"')
    #     dataRest = data.query('type != "Rewards" and type != "Commission"')
    #
    #     # Umwanldung in dataframe
    #     dataRewCom['blockTime'] = pd.to_datetime(dataRewCom['blockTime'], unit='s').dt.date
    #     # sum amounts by date
    #     if 'poolID' in list(dataRewCom.columns.values):
    #         if 'rewardType' in list(dataRewCom.columns.values):
    #             dataRewCom = dataRewCom.groupby(['blockTime', 'type', 'poolID', 'Coin'], as_index=False).agg(
    #                 {'owner': 'last', 'blockHeight': 'first', 'blockHash': 'first', 'Amount': 'sum', 'rewardType': 'first'})
    #         else:
    #             dataRewCom = dataRewCom.groupby(['blockTime', 'type', 'poolID', 'Coin'], as_index=False).agg(
    #                 {'owner': 'last', 'blockHeight': 'first', 'blockHash': 'first', 'Amount': 'sum'})
    #     else:
    #         if 'rewardType' in list(dataRewCom.columns.values):
    #             dataRewCom = dataRewCom.groupby(['blockTime', 'type', 'Coin'], as_index=False).agg({'owner': 'last', 'blockHeight': 'first', 'blockHash': 'first', 'Amount': 'sum','rewardType':'first'})
    #         else:
    #             dataRewCom = dataRewCom.groupby(['blockTime', 'type', 'Coin'], as_index=False).agg({'owner': 'last', 'blockHeight': 'first', 'blockHash': 'first', 'Amount': 'sum'})
    #
    #
    #
    #
    #
    #    # date time to unix timestamp
    #     index = pd.DatetimeIndex(dataRewCom['blockTime'])
    #     index = index.astype(np.int64).to_series() / 1000000000
    #     index = index.reset_index(drop="True")
    #     dataRewCom['blockTime'] = index.astype(int)
    #     dataRewCom['blockTime'] = dataRewCom['blockTime'] + (24*60*60)-1
    #
    #     data = dataRewCom.append(dataRest)
    #
    #     data["amounts"] = data["Amount"].astype(str)+"@"+data['Coin']
    #     data = data.drop(columns=['Amount','Coin'])
    #     data = data.reset_index()
    #
    #     # reorder columns
    #
    #     if 'txid' in list(data.columns.values) and 'poolID' not in list(data.columns.values):
    #         data['poolID'] = '_'
    #     elif 'txid' not in list(data.columns.values) and 'poolID' in list(data.columns.values):
    #         data['txid'] = '_'
    #     elif 'txid' not in list(data.columns.values) and 'poolID' not in list(data.columns.values):
    #         data['poolID'] = '_'
    #         data['txid'] = '_'
    #     if 'rewardType' in list(data.columns.values):
    #          data = data[["blockTime", "owner", "type", "amounts", "blockHash", "blockHeight", "poolID", "txid",'rewardType']]
    #     else:
    #         data = data[["blockTime", "owner", "type", "amounts", "blockHash", "blockHeight", "poolID", "txid"]]
    #
    #     # save to transaction.portfolio
    #     data = data.fillna('_')
    #     data = data.sort_values(by=['blockTime'],ascending=True)
    #
    #     # add to transactio.portfolio
    #     data.to_csv(pathPortfolioData + '/transactionData.portfolio', mode='a', header=False, sep=';', index = False)

    os.remove(pathPortfolioData + '/pythonUpdate.portfolio')