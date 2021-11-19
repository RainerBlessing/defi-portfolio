import sys

from bitcoinrpc.authproxy import AuthServiceProxy
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
######In Zukunt über Übergabeparamter von Java
#     credentials = {}
#     credentials['rpc_username'] = sys.argv[0]
#     credentials['rpc_password'] = sys.argv[1]
#     credentials['rpc_port'] = sys.argv[2]
#     credentials['rpc_hostname'] = sys.argv[3]
#     address = sys.argv[4]
#     maxBlockHeight = sys.argv[5]
#     depth = sys.argv[6]
#     limit = sys.argv[7]
#     numberAddresses = len(sys.argv)
#     addresses tbd. sys.argv[9]...sys.argv[n]

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
    local = 0

    if local == 0:
        rpc_connection = create_connection_rpc(credentials)
        new_block = rpc_connection.getblockcount()
      # ToDo: Loop über adressen
        poolpairs = get_history(address, maxBlockHeight, depth, limit)
        data = pd.DataFrame(poolpairs)

    else:
        data = pd.read_json('C:/Users/Arthur/Desktop/test.json')

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

    # Umwanldung in dataframe
    dataRewCom['blockTime'] = pd.to_datetime(dataRewCom['blockTime'], unit='s').dt.date
    # sum amounts by date
    dataRewCom = dataRewCom.groupby(['blockTime','type','poolID','Coin'], as_index=False).agg({'owner':'last','blockHeight':'first','blockHash':'first','Amount': 'sum'})

    # date time to unix timestamp
    index = pd.DatetimeIndex(dataRewCom['blockTime'])
    index = index.astype(np.int64).to_series() / 1000000000
    index = index.reset_index(drop="True")
    dataRewCom['blockTime'] = index.astype(int)

    data = dataRewCom.append(dataRest)

    data["amounts"] = data["Amount"].astype(str)+"@"+data['Coin']
    data = data.drop(columns=['Amount','Coin'])
    data = data.reset_index()

    # reorder columns
    data = data[["blockTime","owner", "type", "amounts","blockHash","blockHeight","poolID","txid"]]

    # save to transaction.portfolio
    data = data.fillna('_')
    data = data.sort_values(by=['blockTime'],ascending=True)

    # add to transactio.portfolio
    data.to_csv(os.environ.get("APPDATA")+'/defi-portfolio/transactionData.portfolio', mode='a', header=False,sep=';',index = False)


