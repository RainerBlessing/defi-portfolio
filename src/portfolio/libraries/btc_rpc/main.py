import csv
import sys

from bitcoinrpc.authproxy import AuthServiceProxy
from pathlib import Path
import pandas as pd
import os
import numpy as np
import warnings
warnings.simplefilter(action='ignore', category=FutureWarning)
pd.options.mode.chained_assignment = None

def create_connection_rpc(cred):
    url = "http://%s:%s@%s:%s/"%(cred['rpc_username'], cred['rpc_password'], cred['rpc_hostname'], cred['rpc_port'])
    return AuthServiceProxy(url)

def get_history(address,maxBlockHeight,depth,limit):
    poolpairs = rpc_connection.listaccounthistory(address,{"maxBlockHeight": maxBlockHeight,"depth":depth,"no_rewards":False,"limit":limit})
    return poolpairs

if __name__ == '__main__':
    #pfad = sys.argv[1]
    pfad = os.environ.get("APPDATA")+'\\defi-portfolio'

    pathConfig = str(Path.home())+'\\.defi\\defi.conf'
    confFile = pd.read_csv(open(pathConfig),header=None,sep='=')
    confFile=confFile.set_index(0)
    credentials = {}
    credentials['rpc_username'] = confFile.at['rpcuser',1]
    credentials['rpc_password'] = confFile.at['rpcpassword',1]
    if len(confFile.at['rpcport',1][0]) > 1:
        credentials['rpc_port'] = confFile.at['rpcport',1][0]
    else:
        credentials['rpc_port'] = confFile.at['rpcport', 1]
    if len(confFile.at['rpcbind',1][0]) > 1:
        credentials['rpc_hostname'] = confFile.at['rpcbind',1][0]
    else:
        credentials['rpc_hostname'] = confFile.at['rpcbind',1]


    # get local block count
    if os.path.isfile(pfad +'/transactionData.portfolio'):
        transactions = pd.read_csv(open(pfad + '/transactionData.portfolio'),sep=';')
        if transactions.__len__() > 0:
             firstBlock = transactions.iloc[transactions.__len__() - 1, 5]
        else:
            firstBlock = 468146
    else:
        firstBlock = 468146

    # Addresse
    addresses = pd.read_csv(open(pfad + '/Addresses.csv'),header = None)
#################
    local = 1

    if local == 0:
        data = pd.DataFrame()
        rpc_connection = create_connection_rpc(credentials)
        maxBlockHeight = rpc_connection.getblockcount()
        depth = 10000
        limit = depth * 2000
        numAddress = 1
        for iAddress in range(0, addresses.__len__()):
            depth = 10000
            limit = depth * 2000

            # for schleife über blöcke in 10.000 Schritten
            for iBlocks in range(maxBlockHeight, firstBlock, -depth):
                print(((maxBlockHeight-iBlocks)/(maxBlockHeight-firstBlock)))
                f = open(pfad+'/update.portfolio', 'w')
                f.write('Data Update:\n'+addresses.at[iAddress,0]+' ('+str(numAddress)+'/'+str(addresses.__len__())+')\n'+str(round(((maxBlockHeight-iBlocks)/(maxBlockHeight-firstBlock))*100,0))+'%')
                f.close()
                if iBlocks - firstBlock >= depth:
                    poolpairs = get_history(addresses.at[iAddress,0], iBlocks, depth, limit)
                    if data.__len__() == 0:
                        data = pd.DataFrame(poolpairs)
                    else:
                        data = data.append(pd.DataFrame(poolpairs),ignore_index = True)

                if iBlocks - firstBlock < depth:
                    poolpairs = get_history(addresses.at[iAddress, 0], iBlocks, int(iBlocks - firstBlock), limit)
                    if data.__len__() == 0:
                        data = pd.DataFrame(poolpairs)
                    else:
                        data = data.append(pd.DataFrame(poolpairs))
            numAddress = numAddress +1

    else:
        data = pd.read_json('C:/Users/Arthur/Desktop/test.json')

    # Aufsplittung add/remove Pool und poolswap
    swap = data.loc[data['type'] == 'PoolSwap']
    data.drop(data.loc[data['type'] == 'PoolSwap'].index, inplace=True)
    addPool = data.loc[data['type'] == 'AddPoolLiquidity']
    data.drop(data.loc[data['type'] == 'AddPoolLiquidity'].index, inplace=True)
    removePool = data.loc[data['type'] == 'RemovePoolLiquidity']
    data.drop(data.loc[data['type'] == 'RemovePoolLiquidity'].index, inplace=True)
    swapAdd = swap.append(addPool)
    swapAddRemove = swapAdd.append(removePool)
    swapAddRemoveSingles = pd.DataFrame()
    for i in range(0,swapAddRemove.__len__()):
        amounts = swapAddRemove.iloc[i]['amounts']
        for iAmount in range(0,amounts.__len__()):
            swapAddRemove.iloc[i,7]=amounts[iAmount]
            swapAddRemoveSingles = swapAddRemoveSingles.append(swapAddRemove.iloc[i])
    data = data.append(swapAddRemoveSingles)

    data['blockTime'] = data['blockTime'].astype(int)
    data['blockHeight'] = data['blockHeight'].astype(int)

    # Aufsplittung dfi und betrag
    splittedAmount = []
    splittedCoin = []
    rawDataAmount = []
    rawData = data
    for i in data['amounts']:
        if isinstance(i, list):
            splitted = i[0].split('@')
        else:
            splitted = i.split('@')
        splittedAmount.append(float(splitted[0]))
        splittedCoin.append(splitted[1])
        rawDataAmount.append( str(i).replace('[\'', '').replace('\']', ''))
    # export all single data
    rawData['amounts']=rawDataAmount
    rawData = rawData.sort_values(by=['blockTime'],ascending=True)
    rawData = rawData.fillna('_')
    if 'txid' in list(rawData.columns.values):
         rawData = rawData[["blockTime","owner", "type", "amounts","blockHash","blockHeight","poolID","txid"]]
    else:
        rawData = rawData[["blockTime", "owner", "type", "amounts", "blockHash", "blockHeight", "poolID"]]
    rawData.to_csv(pfad+'/rawData.portfolio', mode='a', header=False,sep=';',index = False)
    ###############

    data.insert(len(data.columns), 'Amount', splittedAmount)
    data.insert(len(data.columns), 'Coin', splittedCoin)
    data=data.drop(columns='amounts')


    # split dataFrame into rewards & commissions  and  rest
    dataRewCom = data.query('type == "Rewards" or type == "Commission"')
    dataRest = data.query('type != "Rewards" and type != "Commission"')

    # Umwanldung in dataframe
    dataRewCom['blockTime'] = pd.to_datetime(dataRewCom['blockTime'], unit='s').dt.date
    # sum amounts by date
    dataRewCom = dataRewCom.groupby(['blockTime','type','poolID','Coin'], as_index=False).agg({'owner':'last','blockHeight':'first','blockHash':'first','Amount': 'sum'})

    # date time to unix timestamp
    index = pd.DatetimeIndex(dataRewCom['blockTime'])
    index = index.astype(np.int64).to_series() / 1000000000
    index = index.reset_index(drop="True")
    dataRewCom['blockTime'] = index.astype(int)
    dataRewCom['blockTime'] = dataRewCom['blockTime'] + (24*60*60)-1

    data = dataRewCom.append(dataRest)

    data["amounts"] = data["Amount"].astype(str)+"@"+data['Coin']
    data = data.drop(columns=['Amount','Coin'])
    data = data.reset_index()

    # reorder columns
    if 'txid' in list(data.columns.values):
         data = data[["blockTime","owner", "type", "amounts","blockHash","blockHeight","poolID","txid"]]
    else:
        data = data[["blockTime", "owner", "type", "amounts", "blockHash", "blockHeight", "poolID"]]

    # save to transaction.portfolio
    data = data.fillna('_')
    data = data.sort_values(by=['blockTime'],ascending=True)

    # add to transactio.portfolio
    data.to_csv(pfad+'/transactionData.portfolio', mode='a', header=False,sep=';',index = False)

    os.remove(pfad+'/pythonUpdate.portfolio')