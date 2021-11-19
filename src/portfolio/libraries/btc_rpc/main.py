import sys

from bitcoinrpc.authproxy import AuthServiceProxy
import pandas as pd
import os
import numpy as np

def create_connection_rpc(cred):
    url = "http://%s:%s@%s:%s/"%(cred['rpc_username'], cred['rpc_password'], cred['rpc_hostname'], cred['rpc_port'])
    return AuthServiceProxy(url)

def get_history(address,maxBlockHeight,depth,limit):
    poolpairs = rpc_connection.listaccounthistory(address,{"maxBlockHeight": maxBlockHeight,"depth":depth,"no_rewards":False,"limit":limit})
    return poolpairs

if __name__ == '__main__':
######In Zukunt über Übergabeparamter von Java
    # var = sys.argv[1:]
    # credentials = var[1]
    # listAdresses = var[2]

    credentials = {}
    credentials['rpc_username'] = 'bla'
    credentials['rpc_password'] = 'blabla'
    credentials['rpc_port'] = '8555'
    credentials['rpc_hostname'] = '127.0.0.1'

    address = "dFDKi7d4nHpSqG3vJjPtQ7AmGLaHkH3Fto"
    maxBlockHeight = 900000
    depth = 2000
    limit = 2000
#################

    rpc_connection = create_connection_rpc(credentials)
    new_block = rpc_connection.getblockcount()
   # ToDo: Loop über adressen
    poolpairs = get_history(address, maxBlockHeight, depth, limit)


    data = pd.DataFrame(poolpairs)
  #  data = pd.read_json('C:/Users/Arthur/Desktop/test.json')

    # Umwanldung in dataframe
    data['blockTime'] = pd.to_datetime(data['blockTime'], unit='s').dt.date

    # Aufsplittung dfi und betrag
    splittedAmount = []
    splittedCoin = []
    for i in data['amounts']:
        splitted = i[0].split('@')
        splittedAmount.append(float(splitted[0]))
        splittedCoin.append(splitted[1])
    data.insert(len(data.columns), 'Amount', splittedAmount)
    data.insert(len(data.columns), 'Coin', splittedCoin)
    data=data.drop(columns='amounts')

    # split dataFrame into rewards & commissions  and  rest
    dataRewCom = data.query('type == "Rewards" or type == "Commission"')
    dataRest = data.query('type != "Rewards" and type != "Commission"')
    # sum amounts by date
    dataRewCom = dataRewCom.groupby(['blockTime','type','poolID','Coin'], as_index=False).agg({'owner':'last','blockHeight':'first','blockHash':'first','Amount': 'sum'})

    data = dataRewCom.append(dataRest)

    data["amounts"] = data["Amount"].astype(str)+"@"+data['Coin']
    data = data.drop(columns=['Amount','Coin'])
    data = data.reset_index()

    # date time to unix timestamp
    index = pd.DatetimeIndex(data['blockTime'])
    index = index.astype(np.int64).to_series() / 1000000000
    index = index.reset_index(drop="True")
    data['blockTime'] = index.astype(int)

    # reorder columns
    data = data[["blockTime","owner", "type", "amounts","blockHash","blockHeight","poolID","txid"]]

    # save to transaction.portfolio
    data = data.fillna('_')
    data.to_csv(os.environ.get("APPDATA")+'\\defi-portfolio\\transactionData.portfolio', sep=';',header=False, index=False)
