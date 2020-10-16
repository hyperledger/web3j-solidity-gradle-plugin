pragma solidity ^0.6.0;

import "@openzeppelin/contracts/token/ERC777/ERC777.sol";


contract MyCollectible is ERC721 {
    constructor() ERC721("MyCollectible", "MCO") public {
    }
}