import configparser

config = configparser.ConfigParser()

config['DEFAULT'] = {
    'arquivo': 'estoque.xml',
}

with open('config.ini', 'w') as configfile:
    config.write(configfile)

print("Arquivo config.ini criado com sucesso.")