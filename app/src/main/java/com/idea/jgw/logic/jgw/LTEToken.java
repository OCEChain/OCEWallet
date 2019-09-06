package com.idea.jgw.logic.jgw;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import rx.Observable;
import rx.functions.Func1;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 3.4.0.
 */
public class LTEToken extends Contract {
    private static final String BINARY = "608060405262278d00600355635b1210296004556000600560006101000a81548160ff02191690831515021790555060006009556001600a556000600b60006101000a81548160ff02191690831515021790555033600260006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff160217905550612d12806100a36000396000f300608060405260043610610175576000357c0100000000000000000000000000000000000000000000000000000000900463ffffffff16806306fdde03146103ab578063095ea7b31461043b57806312065fe0146104a057806318160ddd146104cb57806323b872dd146104f657806327e235e31461057b578063313ce567146105d25780633cfba0e3146105fd5780635862fd31146106545780635c6581651461067f57806363a599a4146106f6578063661884631461072557806370a082311461078a57806385c09f26146107e15780638da5cb5b1461080c57806391b7f5ed1461086357806395d89b4114610890578063a035b1fe14610920578063a9059cbb1461094b578063ac56f980146109b0578063c1756a2c146109dd578063c34f783d14610a2a578063c6e6ab0314610b16578063cae9ca5114610bbf578063cbf0b0c014610c6a578063d73dd62314610cad578063da03095c14610d12578063dd62ed3e14610d41578063e06c13dc14610db8575b600080600b60009054906101000a900460ff1615151561019457600080fd5b3491506101e982600860003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002054610de790919063ffffffff16565b600860003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000208190555061024182600954610de790919063ffffffff16565b60098190555061025c600a5483610e0590919063ffffffff16565b90506012600a0a6404a817c8000261027f82600054610de790919063ffffffff16565b1115151561028c57600080fd5b6102a181600054610de790919063ffffffff16565b6000819055506102f981600160003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002054610de790919063ffffffff16565b600160003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020819055507fef7b6e771e9fc3bc19dc09b9c9893f99b49875160d19c24e5022173c0f18b2b83382604051808373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020018281526020019250505060405180910390a15050005b3480156103b757600080fd5b506103c0610e20565b6040518080602001828103825283818151815260200191508051906020019080838360005b838110156104005780820151818401526020810190506103e5565b50505050905090810190601f16801561042d5780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b34801561044757600080fd5b50610486600480360381019080803573ffffffffffffffffffffffffffffffffffffffff16906020019092919080359060200190929190505050610e59565b604051808215151515815260200191505060405180910390f35b3480156104ac57600080fd5b506104b5610f4b565b6040518082815260200191505060405180910390f35b3480156104d757600080fd5b506104e0610fc6565b6040518082815260200191505060405180910390f35b34801561050257600080fd5b50610561600480360381019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190803573ffffffffffffffffffffffffffffffffffffffff16906020019092919080359060200190929190505050610fcf565b604051808215151515815260200191505060405180910390f35b34801561058757600080fd5b506105bc600480360381019080803573ffffffffffffffffffffffffffffffffffffffff1690602001909291905050506114c7565b6040518082815260200191505060405180910390f35b3480156105de57600080fd5b506105e76114df565b6040518082815260200191505060405180910390f35b34801561060957600080fd5b5061063e600480360381019080803573ffffffffffffffffffffffffffffffffffffffff1690602001909291905050506114e4565b6040518082815260200191505060405180910390f35b34801561066057600080fd5b506106696114fc565b6040518082815260200191505060405180910390f35b34801561068b57600080fd5b506106e0600480360381019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190803573ffffffffffffffffffffffffffffffffffffffff169060200190929190505050611502565b6040518082815260200191505060405180910390f35b34801561070257600080fd5b5061070b611527565b604051808215151515815260200191505060405180910390f35b34801561073157600080fd5b50610770600480360381019080803573ffffffffffffffffffffffffffffffffffffffff1690602001909291908035906020019092919050505061153a565b604051808215151515815260200191505060405180910390f35b34801561079657600080fd5b506107cb600480360381019080803573ffffffffffffffffffffffffffffffffffffffff1690602001909291905050506117cb565b6040518082815260200191505060405180910390f35b3480156107ed57600080fd5b506107f6611814565b6040518082815260200191505060405180910390f35b34801561081857600080fd5b50610821611823565b604051808273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200191505060405180910390f35b34801561086f57600080fd5b5061088e60048036038101908080359060200190929190505050611849565b005b34801561089c57600080fd5b506108a56118af565b6040518080602001828103825283818151815260200191508051906020019080838360005b838110156108e55780820151818401526020810190506108ca565b50505050905090810190601f1680156109125780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b34801561092c57600080fd5b506109356118e8565b6040518082815260200191505060405180910390f35b34801561095757600080fd5b50610996600480360381019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190803590602001909291905050506118ee565b604051808215151515815260200191505060405180910390f35b3480156109bc57600080fd5b506109db60048036038101908080359060200190929190505050611c4b565b005b3480156109e957600080fd5b50610a28600480360381019080803573ffffffffffffffffffffffffffffffffffffffff16906020019092919080359060200190929190505050611cb1565b005b348015610a3657600080fd5b50610b14600480360381019080803590602001908201803590602001908080602002602001604051908101604052809392919081815260200183836020028082843782019150505050505091929192908035906020019082018035906020019080806020026020016040519081016040528093929190818152602001838360200280828437820191505050505050919291929080359060200190820180359060200190808060200260200160405190810160405280939291908181526020018383602002808284378201915050505050509192919290505050611de6565b005b348015610b2257600080fd5b50610bbd6004803603810190808035906020019082018035906020019080806020026020016040519081016040528093929190818152602001838360200280828437820191505050505050919291929080359060200190820180359060200190808060200260200160405190810160405280939291908181526020018383602002808284378201915050505050509192919290505050612155565b005b348015610bcb57600080fd5b50610c50600480360381019080803573ffffffffffffffffffffffffffffffffffffffff16906020019092919080359060200190929190803590602001908201803590602001908080601f0160208091040260200160405190810160405280939291908181526020018383808284378201915050505050509192919290505050612384565b604051808215151515815260200191505060405180910390f35b348015610c7657600080fd5b50610cab600480360381019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190505050612546565b005b348015610cb957600080fd5b50610cf8600480360381019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190803590602001909291905050506125bb565b604051808215151515815260200191505060405180910390f35b348015610d1e57600080fd5b50610d3f6004803603810190808035151590602001909291905050506127b7565b005b348015610d4d57600080fd5b50610da2600480360381019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190803573ffffffffffffffffffffffffffffffffffffffff169060200190929190505050612831565b6040518082815260200191505060405180910390f35b348015610dc457600080fd5b50610de56004803603810190808035151590602001909291905050506128b8565b005b6000808284019050838110151515610dfb57fe5b8091505092915050565b6000808284811515610e1357fe5b0490508091505092915050565b6040805190810160405280600d81526020017f4f4345436861696e546f6b656e0000000000000000000000000000000000000081525081565b600081600760003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020819055508273ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff167f8c5be1e5ebec7d5bd14f71427d1e84f3dd0314c0f7b2291e5b200ac8c7c3b925846040518082815260200191505060405180910390a36001905092915050565b6000600260009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff16141515610fa957600080fd5b3073ffffffffffffffffffffffffffffffffffffffff1631905090565b60008054905090565b6000801515600560009054906101000a900460ff161515141515610ff257600080fd5b334261104684600160003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000205461293190919063ffffffff16565b6000600660008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020600101549050600081141515611105576110f66110a585858461294a565b600660008773ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000206000015461293190919063ffffffff16565b821015151561110457600080fd5b5b600073ffffffffffffffffffffffffffffffffffffffff168773ffffffffffffffffffffffffffffffffffffffff161415151561114157600080fd5b600160008973ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002054861115151561118f57600080fd5b600760008973ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002054861115151561121a57600080fd5b61126c86600160008b73ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000205461293190919063ffffffff16565b600160008a73ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000208190555061130186600160008a73ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002054610de790919063ffffffff16565b600160008973ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020819055506113d386600760008b73ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000205461293190919063ffffffff16565b600760008a73ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020819055508673ffffffffffffffffffffffffffffffffffffffff168873ffffffffffffffffffffffffffffffffffffffff167fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef886040518082815260200191505060405180910390a360019450505050509392505050565b60016020528060005260406000206000915090505481565b601281565b60086020528060005260406000206000915090505481565b60095481565b6007602052816000526040600020602052806000526040600020600091509150505481565b600560009054906101000a900460ff1681565b600080600760003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000205490508083111561164b576000600760003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020819055506116df565b61165e838261293190919063ffffffff16565b600760003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020819055505b8373ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff167f8c5be1e5ebec7d5bd14f71427d1e84f3dd0314c0f7b2291e5b200ac8c7c3b925600760003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008873ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020546040518082815260200191505060405180910390a3600191505092915050565b6000600160008373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020549050919050565b6012600a0a6404a817c8000281565b600260009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1681565b600260009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff161415156118a557600080fd5b80600a8190555050565b6040805190810160405280600381526020017f4f4345000000000000000000000000000000000000000000000000000000000081525081565b600a5481565b6000801515600560009054906101000a900460ff16151514151561191157600080fd5b334261196584600160003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000205461293190919063ffffffff16565b6000600660008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020600101549050600081141515611a2457611a156119c485858461294a565b600660008773ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000206000015461293190919063ffffffff16565b8210151515611a2357600080fd5b5b85600160003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000205410151515611a7257600080fd5b600073ffffffffffffffffffffffffffffffffffffffff168773ffffffffffffffffffffffffffffffffffffffff1614151515611aae57600080fd5b611b0086600160003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000205461293190919063ffffffff16565b600160003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002081905550611b9586600160008a73ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002054610de790919063ffffffff16565b600160008973ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020819055508673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff167fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef886040518082815260200191505060405180910390a3600194505050505092915050565b600260009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff16141515611ca757600080fd5b8060048190555050565b6000600260009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff16141515611d0f57600080fd5b600090503073ffffffffffffffffffffffffffffffffffffffff1631821015611d6a578273ffffffffffffffffffffffffffffffffffffffff166108fc839081150290604051600060405180830381858888f1935050505090505b7f9ce9a3d7aa1602561a302eb3577617d0ac1138723fa413856169a1aedf594906838383604051808473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200183815260200182151515158152602001935050505060405180910390a1505050565b600080600260009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff16141515611e4557600080fd5b83518551148015611e57575082518451145b1515611e6257600080fd5b600091505b845182101561214e576012600a0a8483815181101515611e8357fe5b906020019060200201510290506012600a0a6404a817c80002611eb182600054610de790919063ffffffff16565b11151515611ebe57600080fd5b611ed381600054610de790919063ffffffff16565b600081905550611f4281600160008886815181101515611eef57fe5b9060200190602002015173ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002054610de790919063ffffffff16565b600160008785815181101515611f5457fe5b9060200190602002015173ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020819055508482815181101515611faa57fe5b9060200190602002015173ffffffffffffffffffffffffffffffffffffffff1660007fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef836040518082815260200191505060405180910390a361206f8160066000888681518110151561201957fe5b9060200190602002015173ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060000154610de790919063ffffffff16565b60066000878581518110151561208157fe5b9060200190602002015173ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000206000018190555082828151811015156120da57fe5b906020019060200201516006600087858151811015156120f657fe5b9060200190602002015173ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020600101819055508180600101925050611e67565b5050505050565b600080600260009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff161415156121b457600080fd5b6012600a0a6404a817c80002600054111515156121d057600080fd5b600091505b835182101561237e576012600a0a83838151811015156121f157fe5b906020019060200201510290506012600a0a6404a817c8000261221f82600054610de790919063ffffffff16565b1115151561222c57600080fd5b61224181600054610de790919063ffffffff16565b6000819055506122b08160016000878681518110151561225d57fe5b9060200190602002015173ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002054610de790919063ffffffff16565b6001600086858151811015156122c257fe5b9060200190602002015173ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002081905550838281518110151561231857fe5b9060200190602002015173ffffffffffffffffffffffffffffffffffffffff1660007fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef836040518082815260200191505060405180910390a381806001019250506121d5565b50505050565b60006123908484610e59565b508373ffffffffffffffffffffffffffffffffffffffff1660405180807f72656365697665417070726f76616c28616464726573732c75696e743235362c81526020017f616464726573732c627974657329000000000000000000000000000000000000815250602e01905060405180910390207c01000000000000000000000000000000000000000000000000000000009004338530866040518563ffffffff167c0100000000000000000000000000000000000000000000000000000000028152600401808573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020018481526020018373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001828051906020019080838360005b838110156124ea5780820151818401526020810190506124cf565b50505050905090810190601f1680156125175780820380516001836020036101000a031916815260200191505b509450505050506000604051808303816000875af192505050151561253b57600080fd5b600190509392505050565b600260009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff161415156125a257600080fd5b8073ffffffffffffffffffffffffffffffffffffffff16ff5b600061264c82600760003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002054610de790919063ffffffff16565b600760003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020819055508273ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff167f8c5be1e5ebec7d5bd14f71427d1e84f3dd0314c0f7b2291e5b200ac8c7c3b925600760003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008773ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020546040518082815260200191505060405180910390a36001905092915050565b600260009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff1614151561281357600080fd5b6001600b60006101000a81548160ff02191690831515021790555050565b6000600760008473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002054905092915050565b600260009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff1614151561291457600080fd5b80600560006101000a81548160ff02191690831515021790555050565b600082821115151561293f57fe5b818303905092915050565b60008060006129646004548661293190919063ffffffff16565b91506129708483612bb7565b90506001841415612a2f576129da600660008873ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020600001546129d5601984612c8390919063ffffffff16565b612cb6565b612a27600660008973ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020600001546019612cb6565b019250612bae565b6002841415612aec57612a97600660008873ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060000154612a92601984612c8390919063ffffffff16565b612cb6565b612ae4600660008973ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020600001546019612cb6565b019250612bae565b6003841415612ba957612b54600660008873ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060000154612b4f600f84612c8390919063ffffffff16565b612cb6565b612ba1600660008973ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060000154600a612cb6565b019250612bae565b600080fd5b50509392505050565b6000806000806001861415612c0757612bee600c612be060035488610e0590919063ffffffff16565b610e0590919063ffffffff16565b9250600383101515612bff57600392505b829350612c7a565b6002861415612c3e57612c2560035486610e0590919063ffffffff16565b9150600382101515612c3657600391505b819350612c7a565b6003861415612c7557612c5c60035486610e0590919063ffffffff16565b9050600681101515612c6d57600690505b809350612c7a565b600080fd5b50505092915050565b60008082840290506000841480612ca45750828482811515612ca157fe5b04145b1515612cac57fe5b8091505092915050565b6000612cde6064612cd08585612c8390919063ffffffff16565b610e0590919063ffffffff16565b9050929150505600a165627a7a7230582065619291a96ccd30cdfba8716d63028e35c9e921ee1ec37364bbcbfbe7e9018b0029";

    public static final String FUNC_NAME = "name";

    public static final String FUNC_APPROVE = "approve";

    public static final String FUNC_GETBALANCE = "getBalance";

    public static final String FUNC_TOTALSUPPLY = "totalSupply";

    public static final String FUNC_TRANSFERFROM = "transferFrom";

    public static final String FUNC_BALANCES = "balances";

    public static final String FUNC_RATE = "rate";

    public static final String FUNC_DECIMALS = "decimals";

    public static final String FUNC_SETRATE = "setRate";

    public static final String FUNC_ETHBALANCES = "ethBalances";

    public static final String FUNC_GETLOCKBALANCE = "getLockBalance";

    public static final String FUNC_ETHCROWDSALE = "ethCrowdsale";

    public static final String FUNC_EMERGENCYSTOP = "emergencyStop";

    public static final String FUNC_DECREASEAPPROVAL = "decreaseApproval";

    public static final String FUNC_BALANCEOF = "balanceOf";

    public static final String FUNC_TOPTOTALSUPPLY = "topTotalSupply";

    public static final String FUNC_OWNER = "owner";

    public static final String FUNC_SYMBOL = "symbol";

    public static final String FUNC_CHANGEOWNER = "changeOwner";

    public static final String FUNC_TRANSFER = "transfer";

    public static final String FUNC_SETREALSETIME = "setRealseTime";

    public static final String FUNC_SENDETHER = "sendEther";

    public static final String FUNC_ALLOCATETOKEN = "allocateToken";

    public static final String FUNC_ALLOCATECANDYTOKEN = "allocateCandyToken";

    public static final String FUNC_APPROVEANDCALL = "approveAndCall";

    public static final String FUNC_KILL = "kill";

    public static final String FUNC_CROWDSALECLOSED = "crowdsaleClosed";

    public static final String FUNC_INCREASEAPPROVAL = "increaseApproval";

    public static final String FUNC_SETCROWDSALECLOSED = "setCrowdsaleClosed";

    public static final String FUNC_ALLOWANCE = "allowance";

    public static final String FUNC_SETTRANSFEROCE = "setTransferOCE";

    public static final String FUNC_RELEASESTARTTIME = "releaseStartTime";

    public static final String FUNC_ACCEPTNEWOWNER = "acceptNewOwner";

    public static final Event FALLBACKTRIGGED_EVENT = new Event("fallbackTrigged",
            Arrays.<TypeReference<?>>asList(),
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event SENDEVENT_EVENT = new Event("SendEvent",
            Arrays.<TypeReference<?>>asList(),
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Uint256>() {}, new TypeReference<Bool>() {}));
    ;

    public static final Event REPORTCALC_EVENT = new Event("reportCalc",
            Arrays.<TypeReference<?>>asList(),
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event UPDATEOWNER_EVENT = new Event("updateOwner",
            Arrays.<TypeReference<?>>asList(),
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}));
    ;

    public static final Event TRANSFER_EVENT = new Event("Transfer",
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}),
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
    ;

    public static final Event APPROVAL_EVENT = new Event("Approval",
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}),
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
    ;

    protected LTEToken(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected LTEToken(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public RemoteCall<String> name() {
        final Function function = new Function(FUNC_NAME,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<TransactionReceipt> approve(String _spender, BigInteger _value) {
        final Function function = new Function(
                FUNC_APPROVE,
                Arrays.<Type>asList(new Address(_spender),
                        new Uint256(_value)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> getBalance() {
        final Function function = new Function(FUNC_GETBALANCE,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> totalSupply() {
        final Function function = new Function(FUNC_TOTALSUPPLY,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> transferFrom(String _from, String _to, BigInteger _value) {
        final Function function = new Function(
                FUNC_TRANSFERFROM,
                Arrays.<Type>asList(new Address(_from),
                        new Address(_to),
                        new Uint256(_value)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> balances(String param0) {
        final Function function = new Function(FUNC_BALANCES,
                Arrays.<Type>asList(new Address(param0)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> rate() {
        final Function function = new Function(FUNC_RATE,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> decimals() {
        final Function function = new Function(FUNC_DECIMALS,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> setRate(BigInteger _value) {
        final Function function = new Function(
                FUNC_SETRATE,
                Arrays.<Type>asList(new Uint256(_value)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> ethBalances(String param0) {
        final Function function = new Function(FUNC_ETHBALANCES,
                Arrays.<Type>asList(new Address(param0)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> getLockBalance(String _user) {
        final Function function = new Function(FUNC_GETLOCKBALANCE,
                Arrays.<Type>asList(new Address(_user)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> ethCrowdsale() {
        final Function function = new Function(FUNC_ETHCROWDSALE,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<Boolean> emergencyStop() {
        final Function function = new Function(FUNC_EMERGENCYSTOP,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteCall<TransactionReceipt> decreaseApproval(String _spender, BigInteger _subtractedValue) {
        final Function function = new Function(
                FUNC_DECREASEAPPROVAL,
                Arrays.<Type>asList(new Address(_spender),
                        new Uint256(_subtractedValue)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> balanceOf(String _owner) {
        final Function function = new Function(FUNC_BALANCEOF,
                Arrays.<Type>asList(new Address(_owner)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> topTotalSupply() {
        final Function function = new Function(FUNC_TOPTOTALSUPPLY,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<String> owner() {
        final Function function = new Function(FUNC_OWNER,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<String> symbol() {
        final Function function = new Function(FUNC_SYMBOL,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<TransactionReceipt> changeOwner(String _newOwner) {
        final Function function = new Function(
                FUNC_CHANGEOWNER,
                Arrays.<Type>asList(new Address(_newOwner)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> transfer(String _to, BigInteger _value) {
        final Function function = new Function(
                FUNC_TRANSFER,
                Arrays.<Type>asList(new Address(_to),
                        new Uint256(_value)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> setRealseTime(BigInteger _time) {
        final Function function = new Function(
                FUNC_SETREALSETIME,
                Arrays.<Type>asList(new Uint256(_time)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> sendEther(String addr, BigInteger _value) {
        final Function function = new Function(
                FUNC_SENDETHER,
                Arrays.<Type>asList(new Address(addr),
                        new Uint256(_value)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> allocateToken(List<String> _owners, List<BigInteger> _values, List<BigInteger> _addrLockType) {
        final Function function = new Function(
                FUNC_ALLOCATETOKEN,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.DynamicArray<Address>(
                                org.web3j.abi.Utils.typeMap(_owners, Address.class)),
                        new org.web3j.abi.datatypes.DynamicArray<Uint256>(
                                org.web3j.abi.Utils.typeMap(_values, Uint256.class)),
                        new org.web3j.abi.datatypes.DynamicArray<Uint256>(
                                org.web3j.abi.Utils.typeMap(_addrLockType, Uint256.class))),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> allocateCandyToken(List<String> _owners, List<BigInteger> _values) {
        final Function function = new Function(
                FUNC_ALLOCATECANDYTOKEN,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.DynamicArray<Address>(
                                org.web3j.abi.Utils.typeMap(_owners, Address.class)),
                        new org.web3j.abi.datatypes.DynamicArray<Uint256>(
                                org.web3j.abi.Utils.typeMap(_values, Uint256.class))),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> approveAndCall(String _spender, BigInteger _value, byte[] _extraData) {
        final Function function = new Function(
                FUNC_APPROVEANDCALL,
                Arrays.<Type>asList(new Address(_spender),
                        new Uint256(_value),
                        new org.web3j.abi.datatypes.DynamicBytes(_extraData)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> kill(String _addr) {
        final Function function = new Function(
                FUNC_KILL,
                Arrays.<Type>asList(new Address(_addr)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<Boolean> crowdsaleClosed() {
        final Function function = new Function(FUNC_CROWDSALECLOSED,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteCall<TransactionReceipt> increaseApproval(String _spender, BigInteger _addedValue) {
        final Function function = new Function(
                FUNC_INCREASEAPPROVAL,
                Arrays.<Type>asList(new Address(_spender),
                        new Uint256(_addedValue)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> setCrowdsaleClosed(Boolean _bool) {
        final Function function = new Function(
                FUNC_SETCROWDSALECLOSED,
                Arrays.<Type>asList(new Bool(_bool)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> allowance(String _owner, String _spender) {
        final Function function = new Function(FUNC_ALLOWANCE,
                Arrays.<Type>asList(new Address(_owner),
                        new Address(_spender)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> setTransferOCE(Boolean _bool) {
        final Function function = new Function(
                FUNC_SETTRANSFEROCE,
                Arrays.<Type>asList(new Bool(_bool)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> releaseStartTime() {
        final Function function = new Function(FUNC_RELEASESTARTTIME,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> acceptNewOwner() {
        final Function function = new Function(
                FUNC_ACCEPTNEWOWNER,
                Arrays.<Type>asList(),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public List<FallbackTriggedEventResponse> getFallbackTriggedEvents(TransactionReceipt transactionReceipt) {
        List<EventValuesWithLog> valueList = extractEventParametersWithLog(FALLBACKTRIGGED_EVENT, transactionReceipt);
        ArrayList<FallbackTriggedEventResponse> responses = new ArrayList<FallbackTriggedEventResponse>(valueList.size());
        for (EventValuesWithLog eventValues : valueList) {
            FallbackTriggedEventResponse typedResponse = new FallbackTriggedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.addr = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<FallbackTriggedEventResponse> fallbackTriggedEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, FallbackTriggedEventResponse>() {
            @Override
            public FallbackTriggedEventResponse call(Log log) {
                EventValuesWithLog eventValues = extractEventParametersWithLog(FALLBACKTRIGGED_EVENT, log);
                FallbackTriggedEventResponse typedResponse = new FallbackTriggedEventResponse();
                typedResponse.log = log;
                typedResponse.addr = (String) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<FallbackTriggedEventResponse> fallbackTriggedEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(FALLBACKTRIGGED_EVENT));
        return fallbackTriggedEventObservable(filter);
    }

    public List<SendEventEventResponse> getSendEventEvents(TransactionReceipt transactionReceipt) {
        List<EventValuesWithLog> valueList = extractEventParametersWithLog(SENDEVENT_EVENT, transactionReceipt);
        ArrayList<SendEventEventResponse> responses = new ArrayList<SendEventEventResponse>(valueList.size());
        for (EventValuesWithLog eventValues : valueList) {
            SendEventEventResponse typedResponse = new SendEventEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.to = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.value = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.result = (Boolean) eventValues.getNonIndexedValues().get(2).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<SendEventEventResponse> sendEventEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, SendEventEventResponse>() {
            @Override
            public SendEventEventResponse call(Log log) {
                EventValuesWithLog eventValues = extractEventParametersWithLog(SENDEVENT_EVENT, log);
                SendEventEventResponse typedResponse = new SendEventEventResponse();
                typedResponse.log = log;
                typedResponse.to = (String) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.value = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
                typedResponse.result = (Boolean) eventValues.getNonIndexedValues().get(2).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<SendEventEventResponse> sendEventEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(SENDEVENT_EVENT));
        return sendEventEventObservable(filter);
    }

    public List<ReportCalcEventResponse> getReportCalcEvents(TransactionReceipt transactionReceipt) {
        List<EventValuesWithLog> valueList = extractEventParametersWithLog(REPORTCALC_EVENT, transactionReceipt);
        ArrayList<ReportCalcEventResponse> responses = new ArrayList<ReportCalcEventResponse>(valueList.size());
        for (EventValuesWithLog eventValues : valueList) {
            ReportCalcEventResponse typedResponse = new ReportCalcEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse._user = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.transferValue = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.releaseValue = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<ReportCalcEventResponse> reportCalcEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, ReportCalcEventResponse>() {
            @Override
            public ReportCalcEventResponse call(Log log) {
                EventValuesWithLog eventValues = extractEventParametersWithLog(REPORTCALC_EVENT, log);
                ReportCalcEventResponse typedResponse = new ReportCalcEventResponse();
                typedResponse.log = log;
                typedResponse._user = (String) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.transferValue = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
                typedResponse.releaseValue = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<ReportCalcEventResponse> reportCalcEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(REPORTCALC_EVENT));
        return reportCalcEventObservable(filter);
    }

    public List<UpdateOwnerEventResponse> getUpdateOwnerEvents(TransactionReceipt transactionReceipt) {
        List<EventValuesWithLog> valueList = extractEventParametersWithLog(UPDATEOWNER_EVENT, transactionReceipt);
        ArrayList<UpdateOwnerEventResponse> responses = new ArrayList<UpdateOwnerEventResponse>(valueList.size());
        for (EventValuesWithLog eventValues : valueList) {
            UpdateOwnerEventResponse typedResponse = new UpdateOwnerEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse._oldOwner = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse._newOwner = (String) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<UpdateOwnerEventResponse> updateOwnerEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, UpdateOwnerEventResponse>() {
            @Override
            public UpdateOwnerEventResponse call(Log log) {
                EventValuesWithLog eventValues = extractEventParametersWithLog(UPDATEOWNER_EVENT, log);
                UpdateOwnerEventResponse typedResponse = new UpdateOwnerEventResponse();
                typedResponse.log = log;
                typedResponse._oldOwner = (String) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse._newOwner = (String) eventValues.getNonIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<UpdateOwnerEventResponse> updateOwnerEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(UPDATEOWNER_EVENT));
        return updateOwnerEventObservable(filter);
    }

    public List<TransferEventResponse> getTransferEvents(TransactionReceipt transactionReceipt) {
        List<EventValuesWithLog> valueList = extractEventParametersWithLog(TRANSFER_EVENT, transactionReceipt);
        ArrayList<TransferEventResponse> responses = new ArrayList<TransferEventResponse>(valueList.size());
        for (EventValuesWithLog eventValues : valueList) {
            TransferEventResponse typedResponse = new TransferEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse._from = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse._to = (String) eventValues.getIndexedValues().get(1).getValue();
            typedResponse._value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<TransferEventResponse> transferEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, TransferEventResponse>() {
            @Override
            public TransferEventResponse call(Log log) {
                EventValuesWithLog eventValues = extractEventParametersWithLog(TRANSFER_EVENT, log);
                TransferEventResponse typedResponse = new TransferEventResponse();
                typedResponse.log = log;
                typedResponse._from = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse._to = (String) eventValues.getIndexedValues().get(1).getValue();
                typedResponse._value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<TransferEventResponse> transferEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(TRANSFER_EVENT));
        return transferEventObservable(filter);
    }

    public List<ApprovalEventResponse> getApprovalEvents(TransactionReceipt transactionReceipt) {
        List<EventValuesWithLog> valueList = extractEventParametersWithLog(APPROVAL_EVENT, transactionReceipt);
        ArrayList<ApprovalEventResponse> responses = new ArrayList<ApprovalEventResponse>(valueList.size());
        for (EventValuesWithLog eventValues : valueList) {
            ApprovalEventResponse typedResponse = new ApprovalEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse._owner = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse._spender = (String) eventValues.getIndexedValues().get(1).getValue();
            typedResponse._value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<ApprovalEventResponse> approvalEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, ApprovalEventResponse>() {
            @Override
            public ApprovalEventResponse call(Log log) {
                EventValuesWithLog eventValues = extractEventParametersWithLog(APPROVAL_EVENT, log);
                ApprovalEventResponse typedResponse = new ApprovalEventResponse();
                typedResponse.log = log;
                typedResponse._owner = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse._spender = (String) eventValues.getIndexedValues().get(1).getValue();
                typedResponse._value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<ApprovalEventResponse> approvalEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(APPROVAL_EVENT));
        return approvalEventObservable(filter);
    }

    public static RemoteCall<LTEToken> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(LTEToken.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    public static RemoteCall<LTEToken> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(LTEToken.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }

    public static LTEToken load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new LTEToken(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    public static LTEToken load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new LTEToken(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static class FallbackTriggedEventResponse {
        public Log log;

        public String addr;

        public BigInteger amount;
    }

    public static class SendEventEventResponse {
        public Log log;

        public String to;

        public BigInteger value;

        public Boolean result;
    }

    public static class ReportCalcEventResponse {
        public Log log;

        public String _user;

        public BigInteger transferValue;

        public BigInteger releaseValue;
    }

    public static class UpdateOwnerEventResponse {
        public Log log;

        public String _oldOwner;

        public String _newOwner;
    }

    public static class TransferEventResponse {
        public Log log;

        public String _from;

        public String _to;

        public BigInteger _value;
    }

    public static class ApprovalEventResponse {
        public Log log;

        public String _owner;

        public String _spender;

        public BigInteger _value;
    }
}
